/*
 * global.h
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

#ifndef GLOBAL_H
#define GLOBAL_H

#include <QMutex>
#include <QSettings>

class WorkerClient;


/// Die Liste aller Sprachen und Interpreter
extern QHash<QString, QString> commands;

/// Eine Verbindung zum Worker
extern WorkerClient *worker;

/// Die Konfigurationsdatei
extern QSettings *config;
/// Eine Mutex für die Konfigurationsdatei
extern QMutex *configMutex;

/// Der Pfad zu /etc für isolate
extern QString etcPath;

/// gibt den Wert für Timeouts der Konfigurationsdatei zurück
extern int timeout ();

#endif // GLOBAL_H

