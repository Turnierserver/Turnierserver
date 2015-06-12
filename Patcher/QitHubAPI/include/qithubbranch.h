/*
 * qithubbranch.h
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

#ifndef QITHUBBRANCH_H
#define QITHUBBRANCH_H

#include "qithubapi.h"
#include "qithubcommit.h"
#include "qithubrepository.h"

#include <QJsonObject>
#include <QString>

class QitHubBranch
{
	
public:
	QitHubBranch(QitHubAPI *client, const QitHubRepository &repo, const QString &name);
	
	QitHubAPI * client () { return api; }
	QitHubRepository repo () const { return _repo; }
	QString name () const { return _name; }
	
	QitHubCommit latestCommit ();
	
public slots:
	void update() { _info = QJsonObject(); }
	
protected:
	QJsonObject info();
	
private:
	QitHubAPI *api;
	QitHubRepository _repo;
	QString _name;
	
	QJsonObject _info;
	
};

#endif // QITHUBBRANCH_H
