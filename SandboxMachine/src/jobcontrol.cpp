/*
 * jobcontrol.cpp
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

#include "aiexecutor.h"
#include "jobcontrol.h"

#include <stdio.h>

JobControl::JobControl (QObject *parent)
	: QObject(parent)
{
}

JobControl::~JobControl ()
{
	mutex.lock();
	if (current)
	{
		current->terminate();
		delete current;
	}
	// die Mutex muss nicht unlocked werden, es soll eh nichts mehr passieren
}

void JobControl::doJob (const Job &job)
{
	current = new AiExecutor(job.id, job.version, job.uuid);
	connect(current, SIGNAL(finished(QUuid)), this, SLOT(jobFinished(QUuid)));
	current->moveToThread(&aiThread);
	connect(this, SIGNAL(startAi()), current, SLOT(run()));
	aiThread.start();
	emit startAi();
}

void JobControl::addJob (const Job &job)
{
	mutex.lock();
	if (!current)
		doJob(job);
	else
		queue.enqueue(job);
	mutex.unlock();
}

void JobControl::terminateJob (const QUuid &uuid)
{
	mutex.lock();
	if (current && current->uuid() == uuid)
		current->terminate(); // will emit finished() -> aiFinished()
	else
		queue.removeAll(uuid);
	mutex.unlock();
}

void JobControl::killJob (const QUuid &uuid)
{
	mutex.lock();
	if (current && current->uuid() == uuid)
		current->kill(); // will emit finished() -> aiFinished()
	else
		queue.removeAll(uuid);
	mutex.unlock();
}

void JobControl::jobFinished (const QUuid &uuid)
{
	mutex.lock();
	if (current->uuid() == uuid)
	{
		delete current;
		if (queue.isEmpty())
			current = 0;
		else
			doJob(queue.dequeue());
	}
	else
		fprintf(stderr, "Warnung: JobControl: Aktueller Job ist %s aber %s wurde beendet.\n", qPrintable(current->uuid().toString()), qPrintable(uuid.toString()));
	mutex.unlock();
	emit aiFinished(uuid);
}
