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

#include <QCommandLineOption>
#include <QCommandLineParser>
#include <QCoreApplication>
#include <QDebug>
#include <QSettings>
#include <QThread>
#include <QTime>
#include <QTimer>

#include <qithubapi.h>
#include <qithubrepository.h>

#include "patcher.h"

int main(int argc, char *argv[])
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("Turnierserver Patcher");
	
	QCommandLineParser parser;
	parser.setApplicationDescription("Der Patcher lädt automatisch die neueste Version des Turnierservers und der Konfiguration "
									 "herunter, kompiliert die Anwendung, und startet alles. Bei Updates werden die betroffenen "
									 "Anwendungen gestoppt und anschließend neu gestartet.");
	parser.addHelpOption();
	QCommandLineOption backendOption(QStringList() << "b" << "backend", "Wenn angegeben wird das Backend gestartet");
	parser.addOption(backendOption);
	QCommandLineOption workerOption(QStringList() << "w" << "worker", "Wenn angegeben wird ein Worker und die angegebene Anzahl "
																	  "an Sandboxen gestartet.", "sandboxes", "0");
	parser.addOption(workerOption);
	QCommandLineOption frontendOption(QStringList() << "f" << "frontend", "Wenn angegeben wird das Frontend gestartet.");
	parser.addOption(frontendOption);
	QCommandLineOption codrOption(QStringList() << "c" << "codr", QString::fromUtf8("Wenn angegeben wird der Codr bei Änderungen "
								  "neu kompiliert und hochgeladen."));
	parser.addOption(codrOption);
	QCommandLineOption branchOption("branch", "Gibt den zu benutzenden Branch von beiden Repositories an. Default: master. Wird "
									"von den entsprechenden Parametern überschrieben.", "branch", "master");
	parser.addOption(branchOption);
	QCommandLineOption repoBranchOption("repo-branch", "Gibt den zu benutzenden Branch des Repositories an.", "branch");
	parser.addOption(repoBranchOption);
	QCommandLineOption configBranchOption("config-branch", "Gibt den zu benutzenden Branch des Config-Repositories an.", "branch");
	parser.addOption(configBranchOption);
	parser.addPositionalArgument("config-file", QString::fromUtf8("Die Konfigurationsdatei für den Patcher."), "[<config-file>]");
	parser.process(app);
	QStringList args = parser.positionalArguments();
	
	// Die Konfigurationsdatei laden
	QSettings *config;
	if (args.size() > 0)
		config = new QSettings(args[0], QSettings::IniFormat);
	else
		config = new QSettings(QSettings::IniFormat, QSettings::SystemScope, "Pixelgaffer", "Patcher");
	
	// die GitHub Repositories laden
	config->beginGroup("GitHub");
	QitHubAPI api;
	if (!api.connectUsingOAuth(config->value("OAuthToken").toString()))
	{
		fprintf(stderr, "Fehler beim Anmelden bei GitHub.\n");
		return 1;
	}
	config->endGroup();
	
	config->beginGroup("Repository");
	QitHubRepository repo(&api, config->value("User").toString(), config->value("Repo").toString());
	config->endGroup();
	
	config->beginGroup("ConfigRepository");
	QitHubRepository configRepo(&api, config->value("User").toString(), config->value("Repo").toString());
	config->endGroup();
	
	// den Patcher erstellen
	Patcher patcher(config, repo, parser.isSet(repoBranchOption) ? parser.value(repoBranchOption) : parser.value(branchOption),
					configRepo, parser.isSet(configBranchOption) ? parser.value(configBranchOption) : parser.value(branchOption));
	if (parser.isSet(backendOption))
		patcher.startBackend();
	if (parser.isSet(workerOption))
		patcher.startWorker(parser.value(workerOption).toUInt());
	if (parser.isSet(frontendOption))
		patcher.startFrontend();
	if (parser.isSet(codrOption))
		patcher.startCodr();
	
	// warten bis der Patcher starten soll
	QString timeStr = config->value("Time", "now").toString();
	if (timeStr != "now")
	{
		QTime time = QTime::fromString(timeStr, "hh:mm");
		if (!time.isValid())
		{
			fprintf(stderr, "Ungültige Zeitangabe %s\n", qPrintable(timeStr));
			return 1;
		}
		QTime current = QTime::currentTime();
		QThread::sleep(current.secsTo(time));
	}
	
	// den Patcher jetzt einmal starten und dann in dem angegebenen Intervall
	int interval = config->value("Interval").toInt();
	if (interval <= 0)
	{
		fprintf(stderr, "Ungültiges Intervall %d\n", interval);
		return 1;
	}
	patcher.update();
	QTimer timer;
	QObject::connect(&timer, SIGNAL(timeout()), &patcher, SLOT(update()));
	timer.start(interval * 60000);
	
	return app.exec();
}
