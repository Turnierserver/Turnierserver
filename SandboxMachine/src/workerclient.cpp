/*
 * workerclient.cpp
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

#include "aiexecutor.h"
#include "buffer.h"
#include "logger.h"
#include "workerclient.h"

#include <stdio.h>

#include <QBuffer>
#include <QJsonArray>
#include <QJsonDocument>
#include <QJsonObject>
#include <QMessageLogger>
#include <QMetaEnum>
#include <QMetaObject>
#include <QRegularExpression>
#include <QTimer>
#include <QUuid>

#ifndef MAX_BUF_SIZE
#  define MAX_BUF_SIZE 0xa00000
#endif

WorkerClient::WorkerClient(QObject *parent)
	: QObject(parent)
{
	reconnect();
}

void WorkerClient::connected ()
{
	LOG_INFO << "Connected to Worker";
	socket->write("S");
	// Die unterstützten Programmiersprachen schicken
	QJsonArray array;
	for (QString lang : commands.keys())
		array.append(lang);
	QJsonDocument doc(array);
	socket->write(doc.toJson(QJsonDocument::Compact) + "\n");
}

void WorkerClient::disconnected ()
{
	LOG_CRITICAL << "Disconnected from Worker";
	QTimer::singleShot(3000, this, SLOT(reconnect()));
}

void WorkerClient::error (QAbstractSocket::SocketError error)
{
	QMetaObject mo = QAbstractSocket::staticMetaObject;
	QMetaEnum me = mo.enumerator(mo.indexOfEnumerator("SocketError"));
	LOG_CRITICAL << QString("SocketError: ") + me.key(error);
	QTimer::singleShot(3000, this, SLOT(reconnect()));
}

void WorkerClient::reconnect()
{
	if (socket)
		delete socket;
	socket = new QTcpSocket;
	connect(socket, SIGNAL(connected()), this, SLOT(connected()));
	connect(socket, SIGNAL(disconnected()), this, SLOT(disconnected()));
	connect(socket, SIGNAL(error(QAbstractSocket::SocketError)), this, SLOT(error(QAbstractSocket::SocketError)));
	connect(socket, SIGNAL(readyRead()), this, SLOT(readyRead()));
	socket->connectToHost(config->value("Worker/Host").toString(), config->value("Worker/Port").toInt());
}

void WorkerClient::readyRead ()
{
	static Buffer data;
	
	QByteArray read;
	while (true)
	{
		read = socket->read(MAX_BUF_SIZE - data.size());
		if (read.isEmpty())
			break;
		data.append(read);
		
		// alle zeilen von data lesen und auswerten
		while (true)
		{
			read = data.readLine();
			if (read.isEmpty())
				break;
			read = read.trimmed();
			
			QJsonDocument jsondoc = QJsonDocument::fromJson(read);
			QJsonObject json = jsondoc.object();
			QString cmd = json.value("command").toString();
			if (cmd == "R")
			{
				int id = json.value("id").toInt();
				int version = json.value("version").toInt();
				QUuid uuid = json.value("uuid").toVariant().toUuid();
				LOG_INFO << "Auftrag erhalten: Run AI " + QString::number(id) + "v" + QString::number(version) + " " + uuid.toString();
				
				jobControl.addJob(id, version, uuid);
			}
			else if (cmd == "T")
			{
				QUuid uuid = json.value("uuid").toVariant().toUuid();
				LOG_INFO << "Auftrag erhalten: Terminate AI " + uuid.toString();
				jobControl.terminateJob(uuid);
			}
			else if (cmd == "K")
			{
				QUuid uuid = json.value("uuid").toVariant().toUuid();
				LOG_INFO << "Auftrag erhalten: Kill AI " + uuid.toString();
				jobControl.killJob(uuid);
			}
			else
			{
				LOG_DEBUG << "Also es wäre schön wenn ich den Befehl " + cmd + " verstehen würde";
			}
		}
	}
}

void WorkerClient::sendMessage(QUuid uuid, char event)
{
	QJsonObject json;
	json.insert("uuid", uuid.toString().replace(QRegularExpression("[\\{\\}]"), ""));
	json.insert("event", QString(event));
	QJsonDocument jsondoc(json);
	socket->write(jsondoc.toJson(QJsonDocument::Compact));
	socket->write("\n");
	socket->flush();
}
