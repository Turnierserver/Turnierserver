/*
 * aiexecutor.cpp
 * 
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 * 
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 * 
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "aiexecutor.h"

#include <errno.h>
#include <pwd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

#include <QTemporaryDir>

AiExecutor::AiExecutor (int id, int version, const QUuid &uuid)
	: _id(id)
	, _version(version)
	, _uuid(uuid)
{
	// die UID herausfinden
	configMutex->lock();
	config->beginGroup("Sandbox");
	uid = config->value("UID").toInt();
	config->endGroup();
	configMutex->unlock();
	if (uid < 1000)
	{
		fprintf(stderr, "Warnung: Ich werde keine UID kleiner als 1000 benutzen.\n");
		abort = true;
		return;
	}
	
	// Informationen zum User finden
	passwd *userInfo = getpwuid(uid); // nicht löschen
	gid = userInfo->pw_gid;
	
	// ein neues Verzeichnis für den Job im Home des Users anlegen
	QTemporaryDir tmpDir(QString(userInfo->pw_dir) + "/ai-XXXXXX");
	tmpDir.setAutoRemove(false);
	dir = tmpDir.path();
	
	// das Verzeichnis gehört root (die KI darf nichts ändern), die Gruppe auf die des Benutzers setzen
	if (chown(qPrintable(dir.absolutePath()), 0, gid) != 0)
	{
		perror("Fehler: Kann den Benutzer vom KI Verzeichnis nicht ändern");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(dir.absolutePath()));
		abort = true;
		return;
	}
}

void AiExecutor::run ()
{
	if (abort)
	{
		fprintf(stderr, "Weigere mich aufgrund vorheriger Fehler die KI zu laden und zu starten.\n");
		emit finished(uuid());
		return;
	}
	if (!download())
	{
		fprintf(stderr, "Konnte KI nicht herunterladen. Abbruch.\n");
		emit finished(uuid());
		return;
	}
	if (!generateProps())
	{
		fprintf(stderr, "Konnte die KI Properties nicht erstellen. Abbruch.\n");
		emit finished(uuid());
		return;
	}
	execute();
	emit finished(uuid());
}

bool AiExecutor::download ()
{
	binArchive = dir.absoluteFilePath("bin.tar.bz2");
	if (!mirror->retrieveAi(id(), version(), binArchive))
		return false;
	
	const char *cmd = qPrintable("sandboxd_helper -u " + QString::number(uid) + " -g " + QString::number(gid) + " -d \"" + dir.absolutePath() + "\" -c \"bsdtar xfj bin.tar.bz2 -C bin/\"");
	printf("$ %s\n", cmd);
	system(cmd);
}

bool AiExecutor::generateProps ()
{
	aiProp = dir.absoluteFilePath("ai.prop");
	QFile file(aiProp);
	if (!file.open(QIODevice::WriteOnly))
	{
		fprintf(stderr, "Kann die KI Properties nicht beschreiben (%s): %s\n", qPrintable(aiProp), qPrintable(file.errorString()));
		return false;
	}
	file.write("# GENERATED FILE - DO NOT EDIT\n");
	
	configMutex->lock();
	config->beginGroup("Worker");
	file.write("turnierserver.worker.host=" + config->value("Host").toByteArray() + "\n");
	file.write("turnierserver.worker.server.port=" + config->value("Port").toByteArray() + "\n");
	file.write("turnierserver.worker.server.aichar=A\n"); // vlt sollte das in die config der sandbox
	file.write("turnierserver.serializer.compress.worker=" + config->value("AiSerializerCompress").toByteArray() + "\n");
	file.write("turnierserver.ai.uuid=" + uuid().toString().toUtf8() + "\n");
	config->endGroup();
	configMutex->unlock();
	
	file.close();
	return true;
}

bool AiExecutor::execute()
{
	printf("exec ai :)\n");
	return false;
}

void AiExecutor::terminate ()
{
	
}

void AiExecutor::kill ()
{
	
}
