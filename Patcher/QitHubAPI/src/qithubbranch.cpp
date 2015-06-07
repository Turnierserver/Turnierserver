/*
 * qithubbranch.cpp
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

#include "qithubbranch.h"

#include <QJsonDocument>

QitHubBranch::QitHubBranch(QitHubAPI *client, const QitHubRepository &repo, const QString &name)
	: api(client)
	, _repo(repo)
	, _name(name)
{
}

QJsonObject QitHubBranch::info()
{
	if (!_info.isEmpty())
		return _info;
	
	QNetworkRequest req = api->createRequest("/repos/" + repo().user() + "/" + repo().repo() + "/branches/" + name());
	QNetworkReply *reply = api->sendGet(req);
	
	QJsonDocument json = QJsonDocument::fromJson(reply->readAll());
	_info = json.object();
	
	if (reply->error() != QNetworkReply::NoError)
		fprintf(stderr, "Fehler beim Herunterladen von Informationen für %s/%s [%s]: %s\n", qPrintable(repo().user()), qPrintable(repo().repo()), qPrintable(name()), qPrintable(_info.value("message").toString(reply->errorString())));
	
	delete reply;
	return _info;
}

QitHubCommit QitHubBranch::latestCommit()
{
	QJsonObject commit = info().value("commit").toObject();
	return QitHubCommit(api, repo(), commit.value("sha").toString());
}
