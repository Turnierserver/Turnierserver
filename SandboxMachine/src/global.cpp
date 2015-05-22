/*
 * global.cpp
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

#include "mirrorclient.h"
#include "workerclient.h"

#include <QMutex>
#include <QSettings>

/// Eine Verbindung zum Worker
WorkerClient *worker;
/// Eine Verbindung zum Mirror des Workers
MirrorClient *mirror;

/// Die Konfigurationsdatei
QSettings *config;
/// Eine Mutex für die Konfigurationsdatei
QMutex *configMutex = new QMutex;

/// gibt den Wert für Timeouts der Konfigurationsdatei zurück
int timeout ()
{
	configMutex->lock();
	int timeout = config->value("Timeout").toInt();
	configMutex->unlock();
	return timeout;
}
