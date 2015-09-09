/*
 * workerclient.h
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

#ifndef WORKERCLIENT_H
#define WORKERCLIENT_H

#include "jobcontrol.h"

#include <QObject>
#include <QTcpSocket>

/// Diese Klasse definiert slots um mit dem auf dem Worker laufenden Server zu kommunizieren
class WorkerClient : public QObject
{
	Q_OBJECT
	
public:
	/// Initialisiert den Clienten mit dem schon offenen QTcpSocket
	explicit WorkerClient (QObject *parent = 0);
	
public slots:
	// die Slots des QTcpSockets
	void connected ();
	void disconnected ();
	void error (QAbstractSocket::SocketError error);
	void readyRead ();
	
	/// schickt eine Nachricht an den Worker. uuid ist die UUID des Auftrags, event ist das char des Events,
	/// nämlich 'S'tarted Ai (KI gestartet), 'F'inished Ai (KI hat sich selbst beendet), 'T'erminated Ai (KI
	/// auf Auftrag des Backends/Workers/SIGKILL beendet) und 'K'illed Ai (KI auf Auftrag der Logik beendet).
	void sendMessage (QUuid uuid, char event);
	
private slots:
	void reconnect ();
	
private:
	QTcpSocket *socket = 0;
	JobControl jobControl;
	
};

#endif // WORKERCLIENT_H
