/*
 * 
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

#ifndef JOBCONTROL_H
#define JOBCONTROL_H

#include <QMutex>
#include <QObject>
#include <QQueue>
#include <QThread>
#include <QUuid>

class AiExecutor;

/// speichert die ID, Version und UUID eines vom Worker erhaltenen Auftrags
struct Job
{
	// dient zum Löschen eines Auftrags per UUID aus der Queue
	Job(const QUuid &uuid) : uuid(uuid)
	{
	}
	// standart-ctor
	Job(int id, int version, const QUuid &uuid) : id(id), version(version), uuid(uuid)
	{
	}
	
	int id, version;
	QUuid uuid;
	
	/// vergleicht die UUID dieses Auftrags und other
	bool operator== (const Job &other) const { return (uuid == other.uuid); }
};

/// Diese Klasse verwaltet die Jobs vom Worker in einer Queue, die auf der QEventQueue aufbaut
class JobControl : public QObject
{
	Q_OBJECT
	
public:
	explicit JobControl (QObject *parent = 0);
	~JobControl();
	
public slots:
	/// fügt den Job zur Queue hinzu und führt ihn direkt aus wenn die Queue leer ist
	void addJob (const Job &job);
	/// fügt den Job zur Queue hinzu und führt ihn direkt aus wenn die Queue leer ist
	void addJob (int id, int version, const QUuid &uuid) { addJob(Job{id, version, uuid}); }
	
	/// terminiert (= vom Worker/Backend befohlen) den Job mit der uuid
	void terminateJob (const QUuid &uuid);
	/// tötet (= von der Logik befohlen) den Job mit der uuid
	void killJob (const QUuid &uuid);
	
private slots:
	/// wird aufgerufen, wenn der Job mit der uuid fertig ist
	void jobFinished (const QUuid &uuid);
	
signals:
	/// wird emittiert, wenn der aktuell connectete Job starten soll
	/// NICHT VERWENDEN! DIENT NUR ZU INTERNEN ZWECKEN!
	void startAi ();
	
	/// wird emittiert, wenn der Job mit der uuid fertig ist
	void aiFinished (const QUuid &uuid);
	
private:
	/// führt den angegebenen Job aus
	void doJob (const Job &job);
	
	QQueue<Job> queue;
	AiExecutor *current;
	QThread aiThread;
	
	QMutex mutex;
	
};

#endif // JOBCONTROL_H

