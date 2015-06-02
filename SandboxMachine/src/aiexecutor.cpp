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
#include "mirrorclient.h"
#include "workerclient.h"

#include <errno.h>
#include <pwd.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
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
	if (!userInfo)
	{
		fprintf(stderr, "Fehler: Kann Informationen zum Benutzer mit der UID %d nicht abrufen.\n", uid);
		abort = true;
		return;
	}
	gid = userInfo->pw_gid;
	printf("Benutzer: %s:%d:%d:%s:%s\n", userInfo->pw_name, uid, gid, userInfo->pw_dir, userInfo->pw_shell);
	
	// ein neues Verzeichnis für den Job im Home des Users anlegen
	QString dirPath = QString(userInfo->pw_dir) + "/ai-XXXXXX";
	printf("DirPath: %s\n", qPrintable(dirPath));
	QTemporaryDir tmpDir(dirPath);
	tmpDir.setAutoRemove(false);
	printf("TmpDir: %s\n", qPrintable(tmpDir.path()));
	dir = tmpDir.path();
	
	// das Verzeichnis gehört root (die KI darf nichts ändern), die Gruppe auf die des Benutzers setzen
	if (chown(qPrintable(dir.absolutePath()), 0, gid) != 0)
	{
		perror("Fehler: Kann den Benutzer vom KI Verzeichnis nicht ändern");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(dir.absolutePath()));
		abort = true;
		return;
	}
	// die Verzeichnis-Privilegien anpassen
	if (chmod(qPrintable(dir.absolutePath()), S_IRWXU | S_IRGRP | S_IXGRP) != 0)
	{
		perror("Fehler: Kann die Verzeichnisprivilegien nicht auf drwxr-x--- setzen");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(dir.absolutePath()));
		abort = true;
		return;
	}
}

void AiExecutor::runAi ()
{
	if (abort)
	{
		fprintf(stderr, "Weigere mich aufgrund vorheriger Fehler die KI zu laden und zu starten.\n");
		emit finished(uuid());
		return;
	}
	connect(this, SIGNAL(startAi()), this, SLOT(download()));
	connect(this, SIGNAL(downloaded()), this, SLOT(generateProps()));
	connect(this, SIGNAL(propsGenerated()), this, SLOT(executeAi()));
	emit startAi();
}

void AiExecutor::download ()
{
	// das Archiv über den Mirror des Workers herunterladen
	binArchive = dir.absoluteFilePath("bin.tar.bz2");
	mirror->retrieveAi(id(), version(), binArchive);	
	
	// die Privilegien der Archiv-Datei anpassen
	if (chown(qPrintable(binArchive), 0, gid) != 0)
	{
		perror("Fehler: Kann den Benutzer von der binArchiv-Datei nicht ändern");
		fprintf(stderr, "        Datei: %s\n", qPrintable(binArchive));
		abort = true;
		emit downloaded();
		return;
	}
	if (chmod(qPrintable(binArchive), S_IRUSR | S_IWUSR | S_IRGRP) != 0)
	{
		perror("Fehler: Kann die Dateiprivilegien nicht auf drw-r----- setzen");
		fprintf(stderr, "        Datei: %s\n", qPrintable(binArchive));
		abort = true;
		emit downloaded();
		return;
	}
	
	// das bin-Verzeichnis anlegen, zuerst mit Schreibprivilegien für den tar-Befehl
	dir.mkdir("bin");
	binDir = dir.absoluteFilePath("bin");
	if (chown(qPrintable(binDir.absolutePath()), 0, gid) != 0)
	{
		perror("Fehler: Kann den Benutzer vom KI Verzeichnis nicht ändern");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(binDir.absolutePath()));
		abort = true;
		return;
	}
	if (chmod(qPrintable(binDir.absolutePath()), S_IRWXU | S_IRWXG) != 0)
	{
		perror("Fehler: Kann die Verzeichnisprivilegien nicht auf drwxrwx--- setzen");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(binDir.absolutePath()));
		abort = true;
		emit downloaded();
		return;
	}
	
	// tar ausführen
	// wenn ich das nicht als root ausführe kriegt das automatisch die richtigen owner
	QString cmd = "sandboxd_helper -u " + QString::number(uid) + " -g " + QString::number(gid) + " -d \"" + dir.absolutePath()
			+ "\" -c \"bsdtar xfj bin.tar.bz2 --uid 0 --gid " + QString::number(gid) + " -C bin/\"";
	printf("$ %s\n", qPrintable(cmd));
	if (system(qPrintable(cmd)) != 0)
	{
		abort = true;
		emit downloaded();
		return;
	}
	
	// die Schreibprivilegien in das bin-Verzeichnis entfernen
	if (chmod(qPrintable(binDir.absolutePath()), S_IRWXU | S_IRGRP | S_IXGRP) != 0)
	{
		perror("Fehler: Kann die Verzeichnisprivilegien nicht auf drwxr-x--- setzen");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(binDir.absolutePath()));
		abort = true;
		emit downloaded();
		return;
	}
	
	emit downloaded();
}

void AiExecutor::generateProps ()
{
	aiProp = dir.absoluteFilePath("ai.prop");
	QFile file(aiProp);
	if (!file.open(QIODevice::WriteOnly))
	{
		fprintf(stderr, "Kann die KI Properties nicht beschreiben (%s): %s\n", qPrintable(aiProp), qPrintable(file.errorString()));
		abort = true;
		emit propsGenerated();
		return;
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
	
	// die Privilegien anpassen
	if (chown(qPrintable(aiProp), 0, gid) != 0)
	{
		perror("Fehler: Kann den Benutzer von der KI Properties-Datei nicht ändern");
		fprintf(stderr, "        Datei: %s\n", qPrintable(aiProp));
		abort = true;
		emit downloaded();
		return;
	}
	if (chmod(qPrintable(aiProp), S_IRUSR | S_IWUSR | S_IRGRP) != 0)
	{
		perror("Fehler: Kann die Dateiprivilegien nicht auf drw-r----- setzen");
		fprintf(stderr, "        Datei: %s\n", qPrintable(aiProp));
		abort = true;
		emit downloaded();
		return;
	}
	
	emit propsGenerated();
}

void AiExecutor::executeAi ()
{
	pid = fork();
	if (pid < 0)
	{
		perror("Fehler: Kann den aktuellen Prozess nicht spalten");
		worker->sendMessage(uuid(), 'T'); // T weil interner Fehler
		emit finished(uuid());
		return;
	}
	else if (pid == 0)
	{
		QString cmd = "sandboxd_helper -u " + QString::number(uid) + " -g " + QString::number(gid) + " -d \"" + binDir.absolutePath()
				+ "\" -c \"./start.sh " + aiProp + "\"";
//				+ "\" -c id";
		printf("$ %s\n", qPrintable(cmd));
		int retval = system(qPrintable(cmd));
		printf("Die KI hat sich mit dem Statuscode %d beendet.\n", retval);
		worker->sendMessage(uuid(), 'F');
		emit finished(uuid());
		return;
	}
	worker->sendMessage(uuid(), 'S');
}

void AiExecutor::terminateAi ()
{
	kill(pid, SIGKILL);
	worker->sendMessage(uuid(), 'T');
	emit finished(uuid());
}

void AiExecutor::killAi ()
{
	kill(pid, SIGKILL);
	worker->sendMessage(uuid(), 'K');
	emit finished(uuid());
}
