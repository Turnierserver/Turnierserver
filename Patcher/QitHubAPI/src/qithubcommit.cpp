/*
 * qithubcommit.cpp
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

#include "qithubcommit.h"
#include "qithubfile.h"

#include <QJsonArray>
#include <QJsonDocument>

QitHubCommit::QitHubCommit(QitHubAPI *client, const QitHubRepository &repo, const QString &sha)
	: api(client)
	, _repo(repo)
	, _sha(sha)
{
	QNetworkRequest req = api->createRequest("/repos/" + repo.user() + "/" + repo.repo() + "/commits/" + sha);
	QNetworkReply *reply = api->sendGet(req);
	
	QJsonDocument json = QJsonDocument::fromJson(reply->readAll());
	info = json.object();
	
	if (reply->error() != QNetworkReply::NoError)
	fprintf(stderr, "Fehler beim Herunterladen von Informationen für %s/%s %s: %s\n", qPrintable(repo.user()), qPrintable(repo.repo()), qPrintable(sha), qPrintable(info.value("message").toString(reply->errorString())));
	
	delete reply;
}

QList<QitHubCommit> QitHubCommit::parentCommits() const
{
	QJsonArray parents = info.value("parents").toArray();
	QList<QitHubCommit> commits;
	for (int i = 0; i < parents.size(); i++)
	{
		commits << QitHubCommit(api, repo(), parents[i].toObject().value("sha").toString());
	}
	return commits;
}

QList<QitHubFile> QitHubCommit::modifiedFiles() const
{
	QJsonArray files = info.value("files").toArray();
	QList<QitHubFile> modified;
	for (int i = 0; i < files.size(); i++)
	{
		QJsonObject file = files[i].toObject();
		if (file.value("status").toString() == "modified")
			modified << QitHubFile(api, *this, file.value("filename").toString());
	}
	return modified;
}
