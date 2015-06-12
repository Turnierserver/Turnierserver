/*
 * qithubcommit.h
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

#ifndef QITHUBCOMMIT_H
#define QITHUBCOMMIT_H

#include "qithubapi.h"
#include "qithubrepository.h"

#include <QList>
#include <QString>

class QitHubFile;

class QitHubCommit
{
	
public:
	QitHubCommit(QitHubAPI *client, const QitHubRepository &repo, const QString &sha);
	
	QitHubAPI * client () { return api; }
	QitHubRepository repo () const { return _repo; }
	QString sha () const { return _sha; }
	
	QList<QitHubCommit> parentCommits ();
	QList<QitHubFile> modifiedFiles ();
	
public slots:
	void update() { _info = QJsonObject(); }
	
protected:
	QJsonObject info();
	
private:
	QitHubAPI *api;
	QitHubRepository _repo;
	QString _sha;
	
	QJsonObject _info;
	
};

#endif // QITHUBCOMMIT_H
