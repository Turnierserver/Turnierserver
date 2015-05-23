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

#include <QObject>
#include <QTcpSocket>

class MirrorClient : public QObject
{
	Q_OBJECT
	
public:
	explicit MirrorClient (const QString &host, quint16 port, QObject *parent = 0);
	
private:
	bool retrAi (int id, int version, const QString &filename);
	
public slots:
	/// Lädt die KI über den Mirror des Workers herunter und speichert diese in der angegebenen Datei
	void retrieveAi(int id, int version, const QString &filename);
	
signals:
	void aiRetrieved (int id, int version, const QString &filename, bool success);
	
private slots:
	void connected ();
	void disconnected ();
	
private:
	bool reconnect ();
	
	QString _host;
	quint16 _port;
	QTcpSocket socket;
	
};

#endif // MIRRORCLIENT_H
