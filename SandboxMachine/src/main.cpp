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
#include "logger.h"
#include "workerclient.h"

#include <stdio.h>
#include <stdlib.h>

#include <QCommandLineOption>
#include <QCommandLineParser>
#include <QCoreApplication>
#include <QFileInfo>
#include <QHostAddress>
#include <QJsonArray>
#include <QJsonDocument>
#include <QTcpSocket>
#include <QTemporaryDir>

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
			LOG_INFO << language + " gefunden: " + file.absoluteFilePath();
			commands->insert(language, file.absoluteFilePath());
			return;
		}
	}
}

void copyDir (const QDir &src, const QDir &dest)
{
	LOG_DEBUG << "Kopiere Verzeichnis: " + src.absolutePath() +  QString::fromUtf8(" → ") + dest.absolutePath();
	for (QFileInfo fileInfo : src.entryInfoList(QDir::Dirs | QDir::Files | QDir::NoDotAndDotDot))
	{
		if (fileInfo.isDir())
		{
			dest.mkdir(fileInfo.fileName());
			copyDir(src.absoluteFilePath(fileInfo.fileName()), dest.absoluteFilePath(fileInfo.fileName()));
		}
		else
		{
			LOG_DEBUG << "Kopiere Datei: " + src.absoluteFilePath(fileInfo.fileName()) +  QString::fromUtf8(" → ") + dest.absoluteFilePath(fileInfo.fileName());
			QFile::copy(src.absoluteFilePath(fileInfo.fileName()), dest.absoluteFilePath(fileInfo.fileName()));
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
	config->beginGroup("Languages");
	QStringList langs = config->value("langs").toStringList();
	for (QString lang : langs)
	{
		QString command = config->value(lang).toString();
		if (command.isEmpty())
		{
			LOG_INFO << "Erlaube native Sprache " + lang;
			commands.insert(lang, QString());
		}
		else
		{
			LOG_INFO << "Suche Sprache " + lang + " (Befehl " + command + ")";
			searchExecutable(command, lang, &commands);
		}
	}
	config->endGroup();
	
	// isolate-zeugs entpacken
	QTemporaryDir etc(QDir::temp().absoluteFilePath("etc-XXXXXX"));
	etcPath = etc.path();
	copyDir(QDir(":/etc"), etcPath);
	
	// mit dem Worker verbinden
	worker = new WorkerClient();
	
	return app.exec();
}
