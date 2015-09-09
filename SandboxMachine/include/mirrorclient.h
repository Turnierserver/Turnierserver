/*
 * mirrorclient.h
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

#ifndef MIRRORCLIENT_H
#define MIRRORCLIENT_H

#include "global.h"

#include <QFile>
#include <QMutex>
#include <QObject>
#include <QQueue>
#include <QTcpSocket>

struct MirrorRequest
{
	int id, version;
	QString filename;
};

class MirrorClient : public QObject
{
	Q_OBJECT
	
public:
	explicit MirrorClient (const QString &host, quint16 port, QObject *parent = 0);
	
	bool waitForConnected () { return socket.waitForConnected(timeout()); }
	
	QString host () const { return _host; }
	quint16 port () const { return _port; }
	
	bool isConnected () const { return _connected; }
	
	/// Lädt die KI über den Mirror des Workers herunter und speichert diese in der angegebenen Datei
	bool retrieveAi (int id, int version, const QString &filename);
    /// Lädt die Bibliothek über den Mirror des Workers herunter und speichert das Archiv in der angegebenen Datei
    bool retrieveLib (const QString &language, const QString &lib, const QString &filename);
	
private slots:
	void connected ();
	void disconnected ();
	
private:
	void reconnect();
	
	QString _host;
	quint16 _port;
	QTcpSocket socket;
	bool _connected = false;
	
};

#endif // MIRRORCLIENT_H
