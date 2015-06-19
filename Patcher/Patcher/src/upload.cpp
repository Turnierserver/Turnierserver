/*
 * upload.cpp
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

#include "upload.h"

#include <stdio.h>

#include <QDebug>
#include <QEventLoop>
#include <QFile>
#include <QJsonDocument>
#include <QJsonObject>
#include <QMimeDatabase>
#include <QNetworkAccessManager>
#include <QNetworkReply>
#include <QNetworkRequest>

QNetworkAccessManager *mgr = 0;
QMimeDatabase mimeDb;

bool createAndlogin (const QSettings *config)
{
	if (mgr)
		delete mgr;
	mgr = new QNetworkAccessManager;
	
	fprintf(stderr, "Warnung: Benutze unsicheres http protokoll in " __FILE__ "\n");
	QNetworkRequest req("http://" + config->value("Upload/Server").toString() + "/api/login");
	qDebug() << req.url();
	req.setHeader(QNetworkRequest::ContentTypeHeader, "application/json");
	req.setHeader(QNetworkRequest::UserAgentHeader, "Patcher (QtNetwork " QT_VERSION_STR ")");
	QJsonObject json;
	json.insert("email", config->value("Upload/Mail").toString());
	json.insert("password", config->value("Upload/Password").toString());
	QJsonDocument doc(json);
	QEventLoop loop;
	QObject::connect(mgr, SIGNAL(finished(QNetworkReply*)), &loop, SLOT(quit()));
	QNetworkReply *reply = mgr->post(req, doc.toJson(QJsonDocument::Compact));
	loop.exec();
	if (reply->error() != QNetworkReply::NoError)
	{
		fprintf(stderr, "Fehler beim Anmelden: %s\n", qPrintable(reply->errorString()));
		if (reply->header(QNetworkRequest::ContentTypeHeader).toString() == "application/json")
		{
			QJsonDocument jsondoc = QJsonDocument::fromJson(reply->readAll());
			QJsonObject json = jsondoc.object();
			fprintf(stderr, "Fehler: %s\n", qPrintable(json.value("error").toString()));
		}
		else
			fprintf(stderr, "%s\n", reply->readAll().data());
		delete reply;
		return false;
	}
	delete reply;
	return true;
}


bool uploadFile (const QSettings *config, const QString &location, const QString &file)
{
	if (!mgr && !createAndlogin(config))
	{
		if (mgr)
			delete mgr;
		mgr = 0;
		return false;
	}
	
	QString mimeType = mimeDb.mimeTypeForFile(file).name();
	qDebug() << file << ":" << mimeType;
	QFile in(file);
	if (!in.open(QIODevice::ReadOnly))
		return false;
	
	QNetworkRequest req("http://" + config->value("Upload/Server").toString() + location);
	qDebug() << req.url();
	req.setHeader(QNetworkRequest::ContentTypeHeader, mimeType);
	req.setHeader(QNetworkRequest::UserAgentHeader, "Patcher (QtNetwork " QT_VERSION_STR ")");
	QEventLoop loop;
	QObject::connect(mgr, SIGNAL(finished(QNetworkReply*)), &loop, SLOT(quit()));
	QNetworkReply *reply = mgr->post(req, &in);
	loop.exec();
	in.close();
	if (reply->error() != QNetworkReply::NoError)
	{
		fprintf(stderr, "Fehler beim Uploaden: %s\n", qPrintable(reply->errorString()));
		if (reply->header(QNetworkRequest::ContentTypeHeader).toString() == "application/json")
		{
			QJsonDocument jsondoc = QJsonDocument::fromJson(reply->readAll());
			QJsonObject json = jsondoc.object();
			fprintf(stderr, "Fehler: %s\n", qPrintable(json.value("error").toString()));
		}
		else
			fprintf(stderr, "%s\n", reply->readAll().data());
		delete reply;
		return false;
	}
	delete reply;
	qDebug() << "upload finished";
	return true;
}
