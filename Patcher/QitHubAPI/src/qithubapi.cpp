/*
 * qithubapi.cpp
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

#include "qithubapi.h"

#include <stdio.h>

#include <QEventLoop>
#include <QJsonDocument>
#include <QJsonObject>
#include <QRegularExpression>

QitHubAPI::QitHubAPI(const QString host)
	: _host(host)
{
}

QNetworkRequest QitHubAPI::createRequest (const QString &location)
{
	QString url;
	if (location.contains("://"))
		url = location;
	else
		url = "https://" + host() + location;
	QNetworkRequest req(url);
	req.setHeader(QNetworkRequest::UserAgentHeader, "QitHubAPI (QtNetwork " QT_VERSION_STR ")");
	req.setRawHeader("Accept", "application/vnd.github.v3+json");
	if (!_oauth.isEmpty())
		req.setRawHeader("Authorization", "token " + _oauth.toUtf8());
	return req;
}

QNetworkReply * QitHubAPI::sendGet (const QNetworkRequest &req)
{
	QEventLoop loop;
	_mutex.lock();
	QObject::connect(&mgr, SIGNAL(finished(QNetworkReply*)), &loop, SLOT(quit()));
	qithubcall(req.url().toString());
	QNetworkReply *reply = mgr.get(req);
	loop.exec();
	_mutex.unlock();
	
	QUrl redirect = reply->attribute(QNetworkRequest::RedirectionTargetAttribute).toUrl();
	if (!redirect.isEmpty())
	{
		delete reply;
		QNetworkRequest request = createRequest(redirect.toString());
		request.setRawHeader("Referer", redirect.host().toLatin1());
		return sendGet(request);
	}
	
	return reply;
}

QJsonArray QitHubAPI::sendPaginetedGet(const QString &location, int perPage)
{
	return sendPaginetedGet(createRequest(location + (location.contains('?') ? "&" : "?") + "per_page=" + QString::number(perPage)));
}

QJsonArray QitHubAPI::sendPaginetedGet(const QNetworkRequest &req)
{
	QNetworkRequest request = req;
	QNetworkReply *reply = 0;
	QRegularExpression linkRegex("\\s*<(?P<url>[^>]+)>\\s*;\\s*rel=\"?(?P<rel>\\w+)\"?\\s*");
	
	QJsonArray json;
	
	bool cont = true;
	while (cont)
	{
		if (reply)
			delete reply;
		reply = sendGet(request);
		if (!reply || (reply->error() != QNetworkReply::NoError))
			break;
		
		QJsonDocument answerDoc = QJsonDocument::fromJson(reply->readAll());
		QJsonArray answer = answerDoc.array();
		for (int i = 0; i < answer.size(); i++)
			json.append(answer[i]);
		
		QStringList links = QString::fromUtf8(reply->rawHeader("Link")).split(',', QString::SkipEmptyParts);
		cont = false;
		for (QString link : links)
		{
			QRegularExpressionMatch match = linkRegex.match(link);
			if (!match.hasMatch())
				fprintf(stderr, "The server returned a strange Link header: %s\n", qPrintable(link));
			else if (match.captured("rel") == "next")
			{
				request = createRequest(match.captured("url"));
				cont = true;
			}
		}
	}
	
	if (reply)
		delete reply;
	return json;
}

bool QitHubAPI::connectUsingOAuth (const QString &oauth)
{
	_oauth = QString();
	
	QNetworkRequest req = createRequest("/?access_token=" + oauth);
	QNetworkReply *reply = sendGet(req);
	
	if (reply->error() != QNetworkReply::NoError)
	{
		QJsonDocument jsondoc = QJsonDocument::fromJson(reply->readAll());
		QJsonObject json = jsondoc.object();
		fprintf(stderr, "Fehler beim Anmelden: %s\n", qPrintable(json.value("message").toString(reply->errorString())));
		delete reply;
		return false;
	}
	
	delete reply;
	_oauth = oauth;
	return true;
}
