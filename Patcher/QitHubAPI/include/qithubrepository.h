/*
 * qithubrepository.h
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

#ifndef QITHUBREPOSITORY_H
#define QITHUBREPOSITORY_H

#include "qithubapi.h"

#include <QJsonObject>
#include <QString>

class QitHubBranch;
class QitHubCommit;

class QitHubRepository
{
	
public:
	QitHubRepository(QitHubAPI *client, const QString &user, const QString &repo);
	
	QitHubAPI * client () { return api; }
	QString user () const { return _user; }
	QString repo () const { return _repo; }
	
	bool isPrivate () { return info().value("private").toBool(); }
	bool isFork () { return info().value("fork").toBool(); }
	
	QString description () { return QString::fromUtf8(info().value("description").toVariant().toByteArray()); }
	
	QitHubBranch defaultBranch ();
	QList<QitHubBranch> branches () const;
	QList<QitHubCommit> commits () const;
	
public slots:
	void update() { _info = QJsonObject(); }
	
protected:
	QJsonObject info();
	
private:
	QitHubAPI *api;
	QString _user;
	QString _repo;
	
	QJsonObject _info;
	
};

#endif // QITHUBREPOSITORY_H
