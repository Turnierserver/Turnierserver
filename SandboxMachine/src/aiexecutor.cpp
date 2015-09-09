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
#include "logger.h"
#include "mirrorclient.h"
#include "workerclient.h"

#include <error.h>
#include <pwd.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#include <QCoreApplication>
#include <QRegularExpression>
#include <QTemporaryDir>

#define MAX_BOX_ID 100
#define MAX_BOX_ID_RETRIES 1000

AiExecutor::AiExecutor (int id, int version, const QString &lang, const QUuid &uuid)
	: _id(id)
	, _version(version)
	, _lang(lang)
	, _uuid(uuid)
{
	// eine freie id für isolate finden
	for (int i = 0; i < MAX_BOX_ID_RETRIES; i++)
	{
		printf("loop nr. %d\n", i);
		QThread::sleep(1);
		boxid = rand() % MAX_BOX_ID;
		if (!QDir("/tmp/box/" + QString::number(boxid)).exists())
			break;
	}
	
	// isolate initialisieren
	QString cmd = "isolate --cg --init -b " + QString::number(boxid);
	printf("$ %s\n", qPrintable(cmd));
	FILE *isolateInit = popen(qPrintable(cmd), "r");
	if (!isolateInit)
	{
		fprintf(stderr, "Fehler beim initialisieren von isolate\n");
		abort = true;
		return;
	}
	QTextStream isolateInitIn(isolateInit);
	boxdir = isolateInitIn.readAll().trimmed();
	boxdir.mkdir("box");
	dir = boxdir.absoluteFilePath("box/");
}

AiExecutor::~AiExecutor()
{
	disconnect(&proc, SIGNAL(finished(int)), this, SLOT(cleanup(int)));
	if (start)
		delete start;
}

void AiExecutor::runAi ()
{
	if (abort)
	{
		LOG_CRITICAL << "Weigere mich aufgrund vorheriger Fehler die KI zu laden und zu starten";
		emit finished(uuid());
		return;
	}
	connect(this, SIGNAL(startAi()), this, SLOT(download()));
	connect(this, SIGNAL(downloaded()), this, SLOT(generateProps()));
	connect(this, SIGNAL(propsGenerated()), this, SLOT(executeAi()));
	connect(QCoreApplication::instance(), SIGNAL(aboutToQuit()), this, SLOT(terminateAi()));
	emit startAi();
}

void AiExecutor::download ()
{
	if (abort)
	{
		LOG_CRITICAL << "Weigere mich aufgrund vorheriger Fehler die KI zu herunterzuladen";
		emit downloaded();
		return;
	}
	
	// das Archiv über den Mirror des Workers herunterladen
	MirrorClient mirror(config->value("Worker/Host").toString(), config->value("Worker/MirrorPort").toUInt());
	if (!mirror.waitForConnected())
	{
		LOG_CRITICAL << "Fehler: Konnte nicht mit dem Mirror verbinden";
		abort = true;
		emit downloaded();
		return;
	}
	binArchive = dir.absoluteFilePath("bin.tar.bz2");
	if (!mirror.retrieveAi(id(), version(), binArchive))
	{
		LOG_CRITICAL << "Fehler: Konnte KI nicht über den Mirror des Workers herunterladen";
		abort = true;
		emit downloaded();
		return;
	}
	
	// die Privilegien der Archiv-Datei anpassen
	if (chown(qPrintable(binArchive), 0, 0) != 0)
	{
		perror("Fehler: Kann den Benutzer von der binArchiv-Datei nicht ändern");
		fprintf(stderr, "        Datei: %s\n", qPrintable(binArchive));
		abort = true;
		emit downloaded();
		return;
	}
	if (chmod(qPrintable(binArchive), S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH) != 0)
	{
		perror("Fehler: Kann die Dateiprivilegien nicht auf drw-r--r-- setzen");
		fprintf(stderr, "        Datei: %s\n", qPrintable(binArchive));
		abort = true;
		emit downloaded();
		return;
	}
	
	// das bin-Verzeichnis anlegen
	dir.mkdir("bin");
	binDir = dir.absoluteFilePath("bin");
	if (chown(qPrintable(binDir.absolutePath()), 0, 0) != 0)
	{
		perror("Fehler: Kann den Benutzer vom KI Verzeichnis nicht ändern");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(binDir.absolutePath()));
		abort = true;
		emit downloaded();
		return;
	}
	
	// tar ausführen
	QString cmd = "bsdtar xfj " + dir.absoluteFilePath("bin.tar.bz2") + " --uid 0 --gid 0 -C " + binDir.absolutePath();
	printf("$ %s\n", qPrintable(cmd));
	if (system(qPrintable(cmd)) != 0)
	{
		abort = true;
		emit downloaded();
		return;
	}
	
	// die start.ini laden
	start = new QSettings(binDir.absoluteFilePath("start.ini"), QSettings::IniFormat);
	LOG_DEBUG << "Libraries: " + start->value("Libraries").toString();
	for (uint i = 0; i < start->value("Libraries").toUInt(); i++)
	{
		MirrorClient mirror0(config->value("Worker/Host").toString(), config->value("Worker/MirrorPort").toUInt());
		if (!mirror0.waitForConnected())
		{
			LOG_CRITICAL << "Fehler: Konnte nicht mit dem Mirror verbinden";
			abort = true;
			emit downloaded();
			return;
		}
		
		QString path = binDir.absoluteFilePath(start->value("Lib" + QString::number(i) + "/Path").toString());
		binDir.mkdir(path);
		if (chown(qPrintable(path), 0, 0) != 0)
		{
			perror("Fehler: Kann den Benutzer vom KI Verzeichnis nicht ändern");
			fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(path));
			abort = true;
			emit downloaded();
			return;
		}
		
		if (!mirror0.retrieveLib(start->value("Language").toString(), start->value("Lib" + QString::number(i) + "/Name").toString(), path + "/.tar.bz2"))
		{
			LOG_CRITICAL << "Fehler: Konnte Bibliothek nicht über den Mirror des Workers herunterladen";
			abort = true;
			emit downloaded();
			return;
		}
		
		// tar ausführen
		// wenn ich das nicht als root ausführe kriegt das automatisch die richtigen owner
		QString cmd = "bsdtar xfj \"" + path + "/.tar.bz2\" --uid 0 --gid 0 -C \"" + path + "/\"";
		printf("$ %s\n", qPrintable(cmd));
		if (system(qPrintable(cmd)) != 0)
		{
			abort = true;
			emit downloaded();
			return;
		}
		
		if (chmod(qPrintable(path), S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH) != 0)
		{
			perror("Fehler: Kann die Verzeichnisprivilegien nicht auf drwxr-xr-x setzen");
			fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(path));
			abort = true;
			emit downloaded();
			return;
		}
	}
	
	// die Privilegien für das bin-Verzeichnis anpassen
	if (chmod(qPrintable(binDir.absolutePath()), S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH) != 0)
	{
		perror("Fehler: Kann die Verzeichnisprivilegien nicht auf drwxr-xr-x setzen");
		fprintf(stderr, "        Verzeichnis: %s\n", qPrintable(binDir.absolutePath()));
		abort = true;
		emit downloaded();
		return;
	}
	
	emit downloaded();
}

void AiExecutor::generateProps ()
{
	if (abort)
	{
		LOG_CRITICAL << "Weigere mich aufgrund vorheriger Fehler die KI-Properties zu erstellen";
		emit propsGenerated();
		return;
	}
	
	aiProp = dir.absoluteFilePath("ai.prop");
	QFile file(aiProp);
	if (!file.open(QIODevice::WriteOnly))
	{
		LOG_CRITICAL << "Kann die KI Properties nicht beschreiben (" + aiProp + " ): " + file.errorString();
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
	file.write("turnierserver.ai.uuid=" + uuid().toString().replace(QRegularExpression("[\\{\\}]"), "").toUtf8() + "\n");
	config->endGroup();
	configMutex->unlock();
	
	file.close();
	
	// die Privilegien anpassen
	if (chown(qPrintable(aiProp), 0, 0) != 0)
	{
		perror("Fehler: Kann den Benutzer von der KI Properties-Datei nicht ändern");
		fprintf(stderr, "        Datei: %s\n", qPrintable(aiProp));
		abort = true;
		emit propsGenerated();
		return;
	}
	if (chmod(qPrintable(aiProp), S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH) != 0)
	{
		perror("Fehler: Kann die Dateiprivilegien nicht auf drw-r--r-- setzen");
		fprintf(stderr, "        Datei: %s\n", qPrintable(aiProp));
		abort = true;
		emit propsGenerated();
		return;
	}
	
	emit propsGenerated();
}

void AiExecutor::executeAi ()
{
	if (abort)
	{
		LOG_CRITICAL << "Weigere mich aufgrund vorheriger Fehler die KI zu starten";
		worker->sendMessage(uuid(), 'T'); // T weil interner Fehler
		emit finished(uuid());
		return;
	}
	
	proc.setProgram("isolate");
	LOG_DEBUG << "Habe das Programm auf sandboxd_helper gesetzt";
	QString cmd = commands.value(lang());
	if (cmd.isEmpty())
		cmd = start->value("Command").toString();
	LOG_DEBUG << "Der cmd ist " + cmd;
	QStringList args;
	args << "--cg" << "-p";
	args << "--dir=/etc/=" + etcPath;
	args << "--dir=/usr/lib/jvm";
	args << "--run" << "-b" << QString::number(boxid) << "--" << cmd;
	args << start->value("Arguments").toStringList();
	args << aiProp;
	LOG_DEBUG << "Der Befehl ist " + cmd;
	//QStringList args;
	proc.setArguments(args);
	LOG_DEBUG << "Habe die Argumente gesetzt";
	qDebug() << "$" << proc.program() << proc.arguments();
	printf("$ %s %s", qPrintable(proc.program()), qPrintable(QVariant(proc.arguments()).toString()));
	proc.setProcessChannelMode(QProcess::ForwardedChannels);
	//QThread::sleep(1);
	//proc.start("id", args);
	proc.start();
	if (!proc.waitForStarted(timeout()))
	{
		LOG_CRITICAL << "Die KI ist nicht gestartet";
		abort = true;
		worker->sendMessage(uuid(), 'T');
		emit finished(uuid());
		return;
	}
	connect(&proc, SIGNAL(finished(int)), this, SLOT(cleanup(int)));
	
	worker->sendMessage(uuid(), 'S');
}

void AiExecutor::cleanup (int retCode)
{
	LOG_DEBUG << "die KI hat sich mit dem Statuscode " + QString::number(retCode) + " beendet";
	worker->sendMessage(uuid(), 'F');
	emit finished(uuid());
	QString cmd = "isolate --cleanup -b " + QString::number(boxid);
	printf("$ %s\n", qPrintable(cmd));
	system(qPrintable(cmd));
}

void AiExecutor::terminateAi ()
{
	LOG_INFO << "AiExecutor::terminateAi() called";
	//disconnect(&proc, SIGNAL(finished(int)), this, SLOT(cleanup(int)));
	proc.kill();
	if (!proc.waitForFinished())
	{
		perror("Fehler beim Töten der KI");
	}
	worker->sendMessage(uuid(), 'T');
	emit finished(uuid());
}

void AiExecutor::killAi ()
{
	LOG_INFO << "AiExecutor::killAi() called";
	//disconnect(&proc, SIGNAL(finished(int)), this, SLOT(cleanup(int)));
	proc.kill();
	if (!proc.waitForFinished())
	{
		perror("Fehler beim Töten der KI");
	}
	worker->sendMessage(uuid(), 'K');
	emit finished(uuid());
}
