/*
 * qithubfile.cpp
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

#include "qithubfile.h"

#include <QJsonDocument>

QitHubFile::QitHubFile(QitHubAPI *client, const QitHubCommit &commit, const QString &filename)
	: api(client)
	, _commit(commit)
	, _filename(filename)
{
}

QJsonObject QitHubFile::info()
{
	if (!_info.isEmpty())
		return _info;
	
	QNetworkRequest req = api->createRequest("/repos/" + commit().repo().user() + "/" + commit().repo().repo() + "/contents/" + filename() + "?ref=" + commit().sha());
	QNetworkReply *reply = api->sendGet(req);
	
	QJsonDocument json = QJsonDocument::fromJson(reply->readAll());
	_info = json.object();
	
	if (reply->error() != QNetworkReply::NoError)
	{
		fprintf(stderr, "Fehler beim Herunterladen von Informationen für %s/%s %s: %s\n", qPrintable(commit().repo().user()), qPrintable(commit().repo().repo()), qPrintable(filename()), qPrintable(_info.value("message").toString(reply->errorString())));
		_info = QJsonObject();
	}
	
	delete reply;
	return _info;
}

QByteArray QitHubFile::content()
{
	QByteArray base64 = info().value("content").toVariant().toByteArray();
	return QByteArray::fromBase64(base64);
}
