/*
 * qithubfile.h
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

#ifndef QITHUBFILE_H
#define QITHUBFILE_H

#include "qithubapi.h"
#include "qithubcommit.h"

class QitHubFile
{
	
public:
	QitHubFile(QitHubAPI *client, const QitHubCommit &commit, const QString &filename);
	
	QitHubAPI * client () { return api; }
	QitHubRepository repo () const { return _commit.repo(); }
	QitHubCommit commit () const { return _commit; }
	QString filename () const { return _filename; }
	
	QByteArray content () const;
	
private:
	QitHubAPI *api;
	QitHubCommit _commit;
	QString _filename;
	
	QJsonObject info;
	
};

#endif // QITHUBFILE_H
