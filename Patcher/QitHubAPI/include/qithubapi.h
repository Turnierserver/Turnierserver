/*
 * qithubapi.h
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

#ifndef GITHUBAPI_H
#define GITHUBAPI_H

#include "qithubapi_global.h"

#include <QJsonArray>
#include <QMutex>
#include <QNetworkAccessManager>
#include <QNetworkRequest>
#include <QNetworkReply>

class QITHUBAPISHARED_EXPORT QitHubAPI
{
	Q_DISABLE_COPY(QitHubAPI)
	
public:
	QitHubAPI(const QString host = "api.github.com");
	
	QString host () const { return _host; }
	
	bool connectUsingOAuth(const QString &oauth);
	
	virtual QNetworkRequest createRequest (const QString &location);
	virtual QNetworkReply * sendGet (const QNetworkRequest &req);
	virtual QJsonArray sendPaginetedGet(const QString &location, int perPage = 100);
	virtual QJsonArray sendPaginetedGet (const QNetworkRequest &req);
	
private:
	void qithubcall (const QString &location) {	QITHUBCALL(qPrintable(location)); }
	
	QString _host;
	QNetworkAccessManager mgr;
	QMutex _mutex;
	
	QString _oauth;
	
};

#endif // GITHUBAPI_H
