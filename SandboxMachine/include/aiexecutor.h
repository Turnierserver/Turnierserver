/*
 * aiexecutor.h
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

#ifndef AIEXECUTOR_H
#define AIEXECUTOR_H

#include "global.h"

#include <QDir>
#include <QObject>
#include <QUuid>

class AiExecutor : public QObject
{
	Q_OBJECT
	
public:
	explicit AiExecutor (int id, int version, const QUuid &uuid);
	
	int id () const { return _id; }
	int version () const { return _version; }
	
	QUuid uuid () const { return _uuid; }
	
public slots:
	void run ();
	void terminate ();
	void kill ();
	
signals:
	/// wird emittiert wenn die KI terminiert wurde ist
	void finished (const QUuid &uuid);
	
	// die folgenden Signale sind nur für interne Nutzung
	
	/// wird emittiert wenn die KI gestartet werden soll
	void startAi ();
	/// wird emittiert wenn die KI heruntergeladen wurde
	void downloaded ();
	/// wird emittiert wenn die KI Properties erstellt wurden
	void propsGenerated ();
	
protected slots:
	/// fängt an die KI asynchron herunterzuladen
	void download();
	/// verfolständigt den Herunterladungsprozess
	void finishDownload (int id, int version, const QString &filename, bool success);
	/// generiert die KI Properties
	void generateProps();
	/// führt die KI aus
	void execute ();
	
protected:
	int uid, gid;
	
	QDir dir, binDir;
	QString binArchive;
	QString aiProp;
	
private:
	int _id, _version;
	QUuid _uuid;
	
	// wenn Fehler aufgetreten sind
	bool abort = false;

};

#endif // AIEXECUTOR_H
