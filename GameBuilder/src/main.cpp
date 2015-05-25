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

#include "buildinstructions.h"
#include "langspec.h"

#include <stdio.h>

#include <QCommandLineOption>
#include <QCommandLineParser>
#include <QCoreApplication>
#include <QDebug>

int main(int argc, char *argv[])
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("Game Builder");
	
	QCommandLineParser parser;
	parser.setApplicationDescription("Dieses Tool parst eine Datei mit Bauanweisungen zum Bauen eines Spiels für den BwInf Turnierserver.");
	parser.addHelpOption();
	QCommandLineOption fileOption(QStringList() << "f" << "file", "Die Datei mit den Bauanweisungen.", "file", "game.txt");
	parser.addOption(fileOption);
	QCommandLineOption addressOption(QStringList() << "a" << "address", "Die Addresse des Servers, auf den die Daten hochgeladen werden sollen.", "address");
	parser.addOption(addressOption);
	QCommandLineOption verboseOption(QStringList() << "v" << "verbose", "Wenn angegeben werden genauere Informationen ausgegeben.");
	parser.addOption(verboseOption);
	parser.addPositionalArgument("commands", "Die Befehle die ausgeführt werden sollen.", "<command> [<command> ...]");
	parser.process(app);
	QStringList args = parser.positionalArguments();
	
	bool verbose = parser.isSet(verboseOption);
	
	// die Bauanweisungen lesen
	QString file = parser.value(fileOption);
	if (verbose)
		qDebug() << "Lese Bauanweisungen aus Datei" << file;
	BuildInstructions instructions;
	if (!instructions.read(file, verbose))
	{
		fprintf(stderr, "Breche aufgrund vorheriger Fehler ab.\n");
		return 1;
	}
	if (verbose)
		qDebug() << "Bauanweisungen gelesen";
	
	// die LangSpec für Java erstellen (zum Testen)
	LangSpec spec(instructions, "java");
	if (!spec.read(verbose))
	{
		fprintf(stderr, "Breche aufgrund vorheriger Fehler ab.\n");
		return 1;
	}
	
	return 0;
}
