/*
 * evaluator.cpp
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "buildinstructions.h"
#include "evaluator.h"
#include "langspec.h"

#include <stdio.h>

#include <QDebug>
#include <QDir>
#include <QEventLoop>
#include <QHttpMultiPart>
#include <QJsonArray>
#include <QJsonDocument>
#include <QJsonObject>
#include <QNetworkReply>
#include <QNetworkRequest>
#include <QRegularExpression>

#include <quazip5/JlCompress.h>

// zeugs zum passwort lesen
#include <iostream>
#include <string>
#if defined __unix
#  include <termios.h>
#  include <unistd.h>
#elif defined __WIN32 || defined __WIN64
#  include <windows.h>
#endif
using namespace std;

Evaluator::Evaluator(const BuildInstructions &instructions)
	: _instructions(instructions)
	, pwCache(QSettings::IniFormat, QSettings::UserScope, "Pixelgaffer", "GameBuilder")
{
}

Evaluator::~Evaluator()
{
	qDeleteAll(langSpecs);
	if (mgr)
		delete mgr;
}

bool Evaluator::createLangSpecs(bool verbose)
{
	for (QString lang : instructions().langs())
	{
		if (verbose)
			qDebug() << "Erstelle LangSpec für die Sprache" << lang;
		LangSpec *spec = new LangSpec(instructions(), lang);
		if (!spec->read(verbose))
			return false;
		langSpecs << spec;
	}
	return true;
}

int Evaluator::target(const QString &target)
{
	for (LangSpec *spec : langSpecs)
	{
		int ret = this->target(target, spec);
		if (ret != 0)
			return ret;
	}
	return 0;
}

int Evaluator::target(const QString &target, LangSpec *spec)
{
	printf("Baue Ziel %s für %s\n", qPrintable(target), qPrintable(spec->lang()));
	QStringList commands = spec->targetCommands(target);
	for (QString command : commands)
	{
		command = spec->fillVars(command);
		if (command.startsWith("mkdir "))
		{
			QString dir = command.mid(6).trimmed();
			if (!QFileInfo(dir).exists() && !QDir().mkdir(dir))
			{
				fprintf(stderr, "Konnte das Verzeichnis %s nicht anlegen\n", qPrintable(dir));
				return 1;
			}
		}
		else if (command.startsWith("rm "))
		{
			QString file = command.mid(3);
			QFileInfo fileInfo(file);
			bool success = true; // wenn file nicht existiert
			if (fileInfo.isDir())
				success = QDir(file).removeRecursively();
			else if (fileInfo.exists())
				success = QFile(file).remove();
			if (!success)
			{
				fprintf(stderr, "Konnte %s nicht löschen\n", qPrintable(file));
				return 1;
			}
		}
		else if (command.startsWith("exec"))
		{
			QRegularExpression regex("^exec(\\s+in\\s+\"(?P<wd>[^\"]+)\")?\\s+(?P<cmd>[^\r\n]+)\\s*$");
			QRegularExpressionMatch match = regex.match(command);
			if (!match.hasMatch())
			{
				fprintf(stderr, "Syntaxfehler im exec-Befehl\n%s\n     ^\n", qPrintable(command));
				return 1;
			}
			
			QString wd = match.captured("wd");
			QString cwd;
			if (!wd.isEmpty())
			{
				cwd = QDir::currentPath();
				if (!QDir::setCurrent(wd))
				{
					fprintf(stderr, "Fehler beim Ändern des Arbeitsverzeichnises zu %s\n", qPrintable(wd));
					return 1;
				}
			}
			
			QString cmd = match.captured("cmd");
			//qDebug() << "cmd:" << cmd;
			printf("%s$ %s\n", qPrintable(wd), qPrintable(cmd));
			int ret = system(qPrintable(cmd));
			if (ret != 0)
				return ret;
			
			if (!cwd.isEmpty())
			{
				if (!QDir::setCurrent(cwd))
				{
					fprintf(stderr, "Fehler beim Ändern des Arbeitsverzeichnises zu %s\n", qPrintable(cwd));
					return 1;
				}
			}
		}
		else if (command.startsWith("echo "))
		{
			QRegularExpression regex("^echo\\s+\"(?P<text>[^\"]+)\"(\\s+(?P<operator>\\>|\\>\\>)\\s*\"(?P<destination>[^\"]+)\")?\\s*");
			QRegularExpressionMatch match = regex.match(command);
			if (!match.hasMatch())
			{
				fprintf(stderr, "Syntaxfehler im echo-Befehl\n%s\n     ^\n", qPrintable(command));
				return 1;
			}
			
			QString text = match.captured("text");
			QString op = match.captured("operator");
			QString destination = match.captured("destination");
			FILE *file = destination.isEmpty() ? stdout : fopen(qPrintable(destination), op == ">" ? "w" : "a");
			if (!file)
			{
				fprintf(stderr, "Kann Datei %s nicht öffnen\n", qPrintable(destination));
				return 1;
			}
			fprintf(file, "%s\n", qPrintable(text));
			if (!destination.isEmpty())
				fclose(file);
		}
		else if (command.startsWith("upload "))
		{
			QRegularExpression regex("^upload \"(?P<file>[^\"]+)\" to \"(?P<destination>[^\"]+)\"\\s*$");
			QRegularExpressionMatch match = regex.match(command);
			if (!match.hasMatch())
			{
				fprintf(stderr, "Syntaxfehler im upload-Befehö\n%s\n       ^\n", qPrintable(command));
				return 1;
			}
			
			QString file = match.captured("file");
			QFileInfo fileInfo(file);
			if (!fileInfo.exists())
			{
				fprintf(stderr, "%s existiert nicht\n", qPrintable(file));
				return 1;
			}
			QString destination = match.captured("destination");
			
			
			// wenn der NetworkManager 0 ist diesen erstellen
			if (!mgr)
			{
				QTextStream in(stdin);
				mgr = new QNetworkAccessManager;
				
				// testen ob https unterstützt wird
				QNetworkRequest pingRequest("https://" + host + "/api/ping");
				pingRequest.setHeader(QNetworkRequest::UserAgentHeader, "GameBuilder (QtNetwork " QT_VERSION_STR ")");
				QEventLoop loop1;
				QObject::connect(mgr, SIGNAL(finished(QNetworkReply*)), &loop1, SLOT(quit()));
				QNetworkReply *reply = mgr->get(pingRequest);
				loop1.exec();
				https = reply->error() != QNetworkReply::NoError;
				if (https)
					https = reply->readAll().trimmed() == "Pong";
				delete reply;
				if (!https)
					fprintf(stderr, "WARNUNG: Benutze unsicheres http-Protokol\n");
				
				pwCache.beginGroup("pwCache");
				
				// host fragen
				if (host.isEmpty())
				{
					QString def = pwCache.value("host").toString();
					if (def.isEmpty())
						printf("Host: ");
					else
						printf("Host(%s): ", qPrintable(def));
					host = in.readLine();
					if (host.isEmpty())
						host = def;
					pwCache.setValue("host", host);
				}
				// benutzer fragen
				if (email.isEmpty())
				{
					QString def = pwCache.value("mail").toString();
					if (def.isEmpty())
						printf("E-Mail: ");
					else
						printf("E-Mail(%s): ", qPrintable(def));
					email = in.readLine();
					if (email.isEmpty())
						email = def;
					pwCache.setValue("mail", email);
				}
				// passwort fragen
				if (pass.isEmpty())
				{
#if defined __unix
					printf("Passwort: ");
					
					termios oldt;
					tcgetattr(STDIN_FILENO, &oldt);
					termios newt = oldt;
					newt.c_lflag &= ~ECHO;
					tcsetattr(STDIN_FILENO, TCSANOW, &newt);
					
					string s;
					getline(cin, s);
					pass = s.data();
					printf("\n");
					
					tcsetattr(STDIN_FILENO, TCSANOW, &oldt);
#elif defined __WIN32 || defined __WIN64
					printf("Passwort: ");
					
					HANDLE hStdin = GetStdHandle(STD_INPUT_HANDLE); 
					DWORD mode = 0;
					GetConsoleMode(hStdin, &mode);
					SetConsoleMode(hStdin, mode & (~ENABLE_ECHO_INPUT));
					
					string s;
					getline(cin, s);
					pass = s.data();
					
					SetConsoleMode(hStdin, mode);
#elif defined __APPLE__
					char *s = getpass("Passwort: ");
					pass = s;
					
#else
					printf("Passwort (wird auf der Kommandozeile angezeigt): ");
					in >> pass;
#endif
				}
				
				pwCache.endGroup();
				
				// anmelden
				QJsonObject json;
				json.insert("email", email);
				json.insert("password", pass);
				QJsonDocument doc(json);
				QString error = apiPostCall("/api/login", "application/json", doc.toJson(QJsonDocument::Compact)).first;
				if (!error.isEmpty())
				{
					fprintf(stderr, "Fehler beim Anmelden: %s\n", qPrintable(error));
					return 1;
				}
			}
			
			// schauen ob es den gametype schon gibt
			bool gameExists = false; int gameid = -1;
			QPair<QString, QByteArray> result = apiGetCall("/api/gametypes");
			if (!result.first.isEmpty())
			{
				fprintf(stderr, "Fehler beim Abrufen der Spiele: %s\n", qPrintable(result.first));
				return 1;
			}
			QJsonDocument jsonDoc = QJsonDocument::fromJson(result.second);
			QJsonArray gametypes = jsonDoc.array();
			for (int i = 0; i < gametypes.size(); i++)
			{
				QJsonObject gametype = gametypes[i].toObject();
				
				if (gametype.value("name").toString() == instructions().values().value("NAME"))
				{
					gameExists = true;
					gameid = gametype.value("id").toInt();
				}
				
			}
			destination.replace("<gameid>", QString::number(gameid));
			
			if (!gameExists)
			{
				printf("Das Spiel %s existiert noch nicht. Möchtest du es anlegen? [y/n] ", qPrintable(instructions().values().value("NAME")));
				if (getc(stdin) == 'y')
				{
					result = apiPostCall("/api/add_gametype/" + instructions().values().value("NAME"), "application/octet-stream", "");
					if (!result.first.isEmpty())
					{
						fprintf(stderr, "Fehler beim Erstellen des Spiels: %s\n", qPrintable(result.first));
						return 1;
					}
					QJsonDocument jsonDoc = QJsonDocument::fromJson(result.second);
					QJsonObject gameObj = jsonDoc.object();
					gameid = gameObj.value("id").toInt();
					printf("Das neue Spiel wurde erfolgreich angelegt\n");
				}
				else
					return 1;
			}
			
			if (QFileInfo(file).isDir())
			{
				fprintf(stderr, "Baue ZIP-Datei für '%s' ...", qPrintable(file));
				file = createZip(file);
				fprintf(stderr, " '%s'\n", qPrintable(file));
			}
			
			// die Datei hochladen
			QFile in(file);
			if (!in.open(QIODevice::ReadOnly))
			{
				fprintf(stderr, "Kann Datei %s nicht öffnen", qPrintable(file));
				return 1;
			}
			result = apiPostCall(destination, "application/octet-stream", &in);
			if (!result.first.isEmpty())
			{
				fprintf(stderr, "Fehler beim Hochladen von %s: %s\n", qPrintable(file), qPrintable(result.first));
				return 1;
			}
		}
		else
		{
			if (command.contains(QRegularExpression("[^a-z]")))
			{
				fprintf(stderr, "Unknown command: %s\n", qPrintable(command));
				return 1;
			}
			else
			{
				int ret = this->target(command, spec);
				if (ret != 0)
					return ret;
			}
		}
	}
	return 0;
}

QPair<QString, QByteArray> Evaluator::apiGetCall(const QUrl &url)
{
	QNetworkRequest request(url);
	printf("GET %s\n", qPrintable(request.url().toString()));
#ifdef NO_CERT_CHECK
	if (url.scheme() == "https")
	{
		fprintf(stderr, "WARNUNG: Überspringe SSL-Überprüfung\n");
		QSslConfiguration conf = request.sslConfiguration();
		conf.setPeerVerifyMode(QSslSocket::VerifyNone);
		request.setSslConfiguration(conf);
	}
#endif
	request.setHeader(QNetworkRequest::UserAgentHeader, "GameBuilder (QtNetwork " QT_VERSION_STR ")");
	QEventLoop loop;
	QObject::connect(mgr, SIGNAL(finished(QNetworkReply*)), &loop, SLOT(quit()));
	QNetworkReply *reply = mgr->get(request);
	loop.exec();
	QUrl redirect = reply->attribute(QNetworkRequest::RedirectionTargetAttribute).toUrl();
	if(redirect.isValid() && reply->url() != redirect)
	{
	    if(redirect.isRelative())
	        redirect = reply->url().resolved(redirect);
		return apiGetCall(redirect);
	}
	QByteArray c = reply->readAll();
	QString error;
	if (reply->error() != QNetworkReply::NoError)
	{
		fprintf(stderr, "Fehler beim Aufrufen von %s: %s\n", qPrintable(request.url().toString()), qPrintable(reply->errorString()));
		if (reply->header(QNetworkRequest::ContentTypeHeader).toString() == "application/json")
		{
			QJsonDocument jsondoc = QJsonDocument::fromJson(c);
			QJsonObject json = jsondoc.object();
			error = json.value("error").toString();
		}
		else
		{
			error = c.data();
			if (error.isEmpty())
				error = reply->errorString();
		}
	}
	delete reply;
	return qMakePair(error, c);
}

QPair<QString, QByteArray> Evaluator::apiPostCall(const QUrl &url, const QString &contentType, const QByteArray &content)
{
	QNetworkRequest request(url);
	printf("POST %s\n", qPrintable(request.url().toString()));
#ifdef NO_CERT_CHECK
	if (url.scheme() == "https")
	{
		fprintf(stderr, "WARNUNG: Überspringe SSL-Überprüfung\n");
		QSslConfiguration conf = request.sslConfiguration();
		conf.setPeerVerifyMode(QSslSocket::VerifyNone);
		request.setSslConfiguration(conf);
	}
#endif
	request.setHeader(QNetworkRequest::ContentTypeHeader, contentType);
	request.setHeader(QNetworkRequest::UserAgentHeader, "GameBuilder (QtNetwork " QT_VERSION_STR ")");
	QEventLoop loop;
	QObject::connect(mgr, SIGNAL(finished(QNetworkReply*)), &loop, SLOT(quit()));
	QNetworkReply *reply = mgr->post(request, content);
	loop.exec();
	QUrl redirect = reply->attribute(QNetworkRequest::RedirectionTargetAttribute).toUrl();
	if(redirect.isValid() && reply->url() != redirect)
	{
	    if(redirect.isRelative())
	        redirect = reply->url().resolved(redirect);
		return apiPostCall(redirect, contentType, content);
	}
	QByteArray c = reply->readAll();
	QString error;
	if (reply->error() != QNetworkReply::NoError)
	{
		fprintf(stderr, "Fehler beim Aufrufen von %s: %s\n", qPrintable(request.url().toString()), qPrintable(reply->errorString()));
		if (reply->header(QNetworkRequest::ContentTypeHeader).toString() == "application/json")
		{
			QJsonDocument jsondoc = QJsonDocument::fromJson(reply->readAll());
			QJsonObject json = jsondoc.object();
			error = json.value("error").toString();
		}
		else
		{
			error = reply->readAll().data();
			if (error.isEmpty())
				error = reply->errorString();
		}
	}
	delete reply;
	return qMakePair(error, c);
}

QPair<QString, QByteArray> Evaluator::apiPostCall(const QUrl &url, const QString &contentType, QFile *file)
{
	QNetworkRequest request(url);
	printf("POST %s\n", qPrintable(request.url().toString()));
#ifdef NO_CERT_CHECK
	if (url.scheme() == "https")
	{
		fprintf(stderr, "WARNUNG: Überspringe SSL-Überprüfung\n");
		QSslConfiguration conf = request.sslConfiguration();
		conf.setPeerVerifyMode(QSslSocket::VerifyNone);
		request.setSslConfiguration(conf);
	}
#endif
	request.setHeader(QNetworkRequest::ContentTypeHeader, contentType);
	request.setHeader(QNetworkRequest::UserAgentHeader, "GameBuilder (QtNetwork " QT_VERSION_STR ")");
	request.setRawHeader("X-FileName", QFileInfo(file->fileName()).fileName().toUtf8());
	QEventLoop loop;
	QObject::connect(mgr, SIGNAL(finished(QNetworkReply*)), &loop, SLOT(quit()));
	QNetworkReply *reply = mgr->post(request, file);
	loop.exec();
	QUrl redirect = reply->attribute(QNetworkRequest::RedirectionTargetAttribute).toUrl();
	if(redirect.isValid() && reply->url() != redirect)
	{
	    if(redirect.isRelative())
	        redirect = reply->url().resolved(redirect);
		return apiPostCall(redirect, contentType, file);
	}
	QByteArray c = reply->readAll();
	QString error;
	if (reply->error() != QNetworkReply::NoError)
	{
		fprintf(stderr, "Fehler beim Aufrufen von %s: %s\n", qPrintable(request.url().toString()), qPrintable(reply->errorString()));
		if (reply->header(QNetworkRequest::ContentTypeHeader).toString() == "application/json")
		{
			QJsonDocument jsondoc = QJsonDocument::fromJson(reply->readAll());
			QJsonObject json = jsondoc.object();
			error = json.value("error").toString();
		}
		else
		{
			error = reply->readAll().data();
			if (error.isEmpty())
				error = reply->errorString();
		}
	}
	delete reply;
	return qMakePair(error, c);
}

QString Evaluator::createZip(const QDir &dir, QString filename)
{
	if (filename.isEmpty())
		filename = dir.absolutePath() + ".zip";
	
	JlCompress::compressDir(filename, dir.absolutePath());
	
	return filename;
}
