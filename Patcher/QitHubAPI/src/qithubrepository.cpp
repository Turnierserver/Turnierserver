/*
 * qithubrepository.cpp
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
#include "qithubcommit.h"
#include "qithubrepository.h"

#include <QJsonArray>
#include <QJsonDocument>

QitHubRepository::QitHubRepository(QitHubAPI *client, const QString &user, const QString &repo)
	: api(client)
	, _user(user)
	, _repo(repo)
{
}

QJsonObject QitHubRepository::info()
{
	if (!_info.isEmpty())
		return _info;
	
	QNetworkRequest req = api->createRequest("/repos/" + user() + "/" + repo());
	QNetworkReply *reply = api->sendGet(req);
	
	QJsonDocument json = QJsonDocument::fromJson(reply->readAll());
	_info = json.object();
	
	if (reply->error() != QNetworkReply::NoError)
	{
		fprintf(stderr, "Fehler beim Herunterladen von Informationen für %s/%s: %s\n", qPrintable(user()), qPrintable(repo()), qPrintable(_info.value("message").toString(reply->errorString())));
		_info = QJsonObject();
	}
	
	delete reply;
	return _info;
}

QitHubBranch QitHubRepository::defaultBranch()
{
	return QitHubBranch(api, *this, QString::fromUtf8(info().value("default_branch").toVariant().toByteArray()));
}

QList<QitHubBranch> QitHubRepository::branches() // muss paginated werden
{
	QNetworkRequest req = api->createRequest("/repos/" + user() + "/" + repo() + "/branches");
	QNetworkReply *reply = api->sendGet(req);
	
	QList<QitHubBranch> allBranches;
	QJsonDocument json = QJsonDocument::fromJson(reply->readAll());
	
	if (reply->error() != QNetworkReply::NoError)
	{
		QJsonObject error = json.object();
		fprintf(stderr, "Fehler beim Herunterladen von Informationen für %s/%s/branches: %s\n", qPrintable(user()), qPrintable(repo()), qPrintable(error.value("message").toString(reply->errorString())));
		delete reply;
		return allBranches;
	}
	
	QJsonArray branches = json.array();
	for (int i = 0; i < branches.size(); i++)
	{
		QJsonObject branch = branches[i].toObject();
		allBranches << QitHubBranch(api, *this, QString::fromUtf8(branch.value("name").toVariant().toByteArray()));
	}
	
	delete reply;
	return allBranches;
}

QList<QitHubCommit> QitHubRepository::commits() // muss paginated werden
{
	QJsonArray commits = api->sendPaginetedGet("/repos/" + user() + "/" + repo() + "/commits");
	QList<QitHubCommit> allCommits;
	
	for (int i = 0; i < commits.size(); i++)
	{
		QJsonObject branch = commits[i].toObject();
		allCommits << QitHubCommit(api, *this, QString::fromUtf8(branch.value("sha").toVariant().toByteArray()));
	}
	
	return allCommits;
}
