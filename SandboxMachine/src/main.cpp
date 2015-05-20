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

#define DEFAULT_WORKER_PORT 1337

void executable (const QString &execfile, const QString &language, QHash<QString, QString> *commands)
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

void searchJava (QHash<QString, QString> *commands)
{
	executable("java", "Java", commands);
}

void searchPython (QHash<QString, QString> *commands)
{
	executable("python", "Python", commands);
}

int main(int argc, char *argv[])
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("BwInf Turnierserver - Sandbox");
	
	QCommandLineParser parser;
	parser.addHelpOption();
	QCommandLineOption portOption(QStringList() << "p" << "port", "Der Port des Servers auf dem Worker. Default: " + QString::number(DEFAULT_WORKER_PORT), "port", QString::number(DEFAULT_WORKER_PORT));
	parser.addOption(portOption);
	QCommandLineOption addressOption(QStringList() << "a" << "address", "Die Addresse des Backends.", "ip", "::1");
	parser.addOption(addressOption);
	parser.process(app);
	
	// Den Rechner nach Programmiersprachen durchsuchen.
	QHash<QString, QString> commands;
	searchJava(&commands);
	searchPython(&commands);
	
	// Mit dem Worker verbinden
	QString address = parser.value(addressOption);
	quint16 port = parser.value(portOption).toUInt();
	QTcpSocket client;
	client.connectToHost(address, port);
	if (!client.waitForConnected(1000))
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
	client.waitForBytesWritten(1000);
	
	WorkerClient *wclient = new WorkerClient(&client);
	QObject::connect(&client, SIGNAL(connected()), wclient, SLOT(connected()));
	QObject::connect(&client, SIGNAL(disconnected()), wclient, SLOT(disconnected()));
	QObject::connect(&client, SIGNAL(readyRead()), wclient, SLOT(readyRead()));
	
	return app.exec();
}
