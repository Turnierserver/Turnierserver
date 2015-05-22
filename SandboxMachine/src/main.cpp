/*
 * main.cpp
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

#include "global.h"

#include "mirrorclient.h"
#include "workerclient.h"

#include <QCommandLineOption>
#include <QCommandLineParser>
#include <QCoreApplication>
#include <QFileInfo>
#include <QHostAddress>
#include <QJsonArray>
#include <QJsonDocument>
#include <QTcpSocket>

#include <stdio.h>
#include <stdlib.h>

void searchExecutable (const QString &execfile, const QString &language, QHash<QString, QString> *commands)
{
	QStringList pathes = QString(getenv("PATH")).split(':');
	for (QString path : pathes)
	{
		QFileInfo file(path + "/" + execfile);
		while (file.isSymLink())
			file = file.symLinkTarget();
		if (file.exists() && file.isExecutable())
		{
			printf("%s gefunden: %s\n", qPrintable(language), qPrintable(file.absoluteFilePath()));
			commands->insert(language, file.absoluteFilePath());
			return;
		}
	}
}

int main(int argc, char *argv[])
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("BwInf Turnierserver - Sandbox");
	
	QCommandLineParser parser;
	parser.addHelpOption();
	parser.addPositionalArgument("config-file", "Die Konfigurationsdatei für die Sandbox.", "[<config-file>]");
	parser.process(app);
	QStringList args = parser.positionalArguments();
	
	// Die Konfigurationsdatei laden
	if (args.size() > 0)
		config = new QSettings(args[0], QSettings::IniFormat);
	else
		config = new QSettings(QSettings::IniFormat, QSettings::SystemScope, "Pixelgaffer", "SandboxMachine");
	
	// Den Rechner nach Programmiersprachen durchsuchen.
	QHash<QString, QString> commands;
	config->beginGroup("Languages");
	QStringList langs = config->value("langs").toStringList();
	for (QString lang : langs)
	{
		QString command = config->value(lang).toString();
		if (command.isEmpty())
		{
			printf("Erlaube native Sprache %s\n", qPrintable(lang));
			commands.insert(lang, QString());
		}
		else
		{
			printf("Suche Sprache %s (Befehl %s)\n", qPrintable(lang), qPrintable(command));
			searchExecutable(command, lang, &commands);
		}
	}
	config->endGroup();
	
	// Mit dem Worker verbinden
	config->beginGroup("Worker");
	QString host = config->value("Host").toString();
	quint16 port = config->value("Port").toUInt();
	config->endGroup();
	QTcpSocket client;
	client.connectToHost(host, port);
	if (!client.waitForConnected(timeout()))
	{
		fprintf(stderr, "Failed to connect to Worker: %s\n", qPrintable(client.errorString()));
		return 1;
	}
	
	// Dem Worker mitteilen, dass dies eine Sandbox ist
	client.write("S\n");
	
	// Die unterstützten Programmiersprachen schicken
	QJsonArray array;
	for (QString lang : commands.keys())
		array.append(lang);
	QJsonDocument doc(array);
	client.write(doc.toJson(QJsonDocument::Compact) + "\n");
	client.waitForBytesWritten(timeout());
	
	// Mit dem Mirror verbinden
	config->beginGroup("Worker");
	MirrorClient mirror(host, config->value("MirrorPort").toUInt());
	config->endGroup();
	
	// zum testen mal was runterladen
	mirror.retrieveAi(6, 1, "test.tar.bz2");
	
	WorkerClient *wclient = new WorkerClient(&client);
	QObject::connect(&client, SIGNAL(connected()), wclient, SLOT(connected()));
	QObject::connect(&client, SIGNAL(disconnected()), wclient, SLOT(disconnected()));
	QObject::connect(&client, SIGNAL(readyRead()), wclient, SLOT(readyRead()));
	
	return app.exec();
}
