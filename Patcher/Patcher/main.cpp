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

#include <qithubapi.h>
#include <qithubbranch.h>
#include <qithubfile.h>
#include <qithubrepository.h>

int main(int argc, char *argv[])
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("Turnierserver Patcher");
	
	QCommandLineParser parser;
	parser.addHelpOption();
	parser.addPositionalArgument("config-file", "Die Konfigurationsdatei für den Patcher.", "[<config-file>]");
	parser.process(app);
	QStringList args = parser.positionalArguments();
	
	// Die Konfigurationsdatei laden
	QSettings *config;
	if (args.size() > 0)
		config = new QSettings(args[0], QSettings::IniFormat);
	else
		config = new QSettings(QSettings::IniFormat, QSettings::SystemScope, "Pixelgaffer", "Patcher");
	
	config->beginGroup("GitHub");
	QitHubAPI api;
	if (!api.connectUsingOAuth(config->value("OAuthToken").toString()))
		return 1;
	config->endGroup();
	
	config->beginGroup("Repository");
	QitHubRepository repo(&api, config->value("User").toString(), config->value("Repo").toString());
	qDebug() << repo.description();
	QitHubBranch defaultBranch = repo.defaultBranch();
	qDebug() << defaultBranch.name();
	QitHubCommit latestCommit = defaultBranch.latestCommit();
	qDebug() << latestCommit.sha();
	QList<QitHubFile> files = latestCommit.modifiedFiles();
	for (QitHubFile file : files)
	{
		qDebug() << file.filename();
	}
	//qDebug() << files[0].content();

	QList<QitHubCommit> commits = repo.commits();
	qDebug() << commits.size();
	
	config->endGroup();
	
	return 0;
//	return app.exec();
}
