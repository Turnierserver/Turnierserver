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
#include "workerclient.h"

#include <stdio.h>

#include <QBuffer>
#include <QJsonDocument>
#include <QJsonObject>
#include <QRegularExpression>
#include <QUuid>

#ifndef MAX_BUF_SIZE
#  define MAX_BUF_SIZE 0xa00000
#endif

WorkerClient::WorkerClient(QTcpSocket *client, QObject *parent)
	: QObject(parent)
	, socket(client)
{
}

void WorkerClient::connected ()
{
	printf("connected :)\n");
}

void WorkerClient::disconnected ()
{
	printf("disconnected :(\n");
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
				printf("Auftrag erhalten: Run AI %dv%d %s\n", id, version, qPrintable(uuid.toString()));
				
				jobControl.addJob(id, version, uuid);
			}
			else
			{
				fprintf(stderr, "Also es wäre schön wenn ich den Befehl %s verstehen würde\n", qPrintable(cmd));
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
