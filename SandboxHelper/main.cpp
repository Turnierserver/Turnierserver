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

#include <errno.h>
#include <pwd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

#include <QCommandLineOption>
#include <QCommandLineParser>
#include <QCoreApplication>
#include <QDir>

int main(int argc, char *argv[])
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("BwInf Turnierserver - Sandbox Helper");
	
	QCommandLineParser parser;
	parser.setApplicationDescription("Diese Anwendung führt einen Befehl unter besonderen Sicherheitsvorkehrungen aus. Dazu gehört aktuell nur ein "
									 "anderer Benutzer und eine andere Gruppe. Später soll dieser Befehl auch noch Support für die Sandbox enthalten.");
	parser.addHelpOption();
	QCommandLineOption uidOption(QStringList() << "u" << "uid", "Die UID mit der der Befehl ausgeführt werden soll.", "uid");
	parser.addOption(uidOption);
	QCommandLineOption gidOption(QStringList() << "g" << "gid", "Die GID mit der der Befehl ausgeführt werden soll. Wenn GID=0 wird die Standart-Anmelde-GID "
																"des Benutzers genommen.", "gid", "0");
	parser.addOption(gidOption);
	QCommandLineOption dirOption(QStringList() << "d" << "dir", "Das Verzeichnis in dem der Befehl ausgeführt werden soll. Wenn das Verzeichnis nicht existiert, "
																"wird das Home-Verzeichnis des angegebenen Benutzers benutzt.", "dir");
	parser.addOption(dirOption);
	QCommandLineOption commandOption(QStringList() << "c" << "command", "Der Befehl der ausgeführt werden soll.", "cmd");
	parser.addOption(commandOption);
	parser.process(app);
	
	// uid rausfinden
	uid_t uid = parser.value(uidOption).toInt();
	// keine Anmeldung als root zulassen
	if (uid == 0)
	{
		fprintf(stderr, "Fehler: Werde den Befehl nicht als UID=0 ausführen.\n");
		return 1;
	}
	// Benutzerinfos holen
	passwd *userInfo = getpwuid(uid);
	if (!userInfo)
	{
		perror("Kann die Informationen für den Benutzer nicht abrufen");
		return 1;
	}
	
	// gid rausfinden
	gid_t gid = parser.value(gidOption).toInt();
	// wenn die gid 0 ist, die standart-gid des Benutzers benutzen
	if (gid == 0)
	{
		gid = userInfo->pw_gid;
		fprintf(stderr, "Benutze Standard-Anmelde-GID des Benutzers: %d\n", gid);
	}
	
	// gid setzen
	if (setgid(gid) != 0)
	{
		perror("Fehler beim Ändern der GID");
		return 1;
	}
	// uid setzen
	if (setuid(uid) != 0)
	{
		perror("Fehler beim Ändern der UID");
		return 1;
	}
	
	// dir rausfinden
	QString dir = parser.value(dirOption);
	// wenn das Verzeichnis nicht existiert das Home-Verzeichnis benutzen
	if (dir.isEmpty() || !QDir(dir).exists())
	{
		dir = userInfo->pw_dir;
		fprintf(stderr, "Führe den Befehl im Home-Verzeichnis des Benutzers aus: %s\n", qPrintable(dir));
	}
	// das Verzeichnis betreten
	if (chdir(qPrintable(dir)) != 0)
	{
		perror("Fehler beim Wechseln des Verzeichnises");
		return 1;
	}
	
	// den befehl rausfinden und ausführen
	const char *cmd = qPrintable(parser.value(commandOption));
	return system(cmd);
}
