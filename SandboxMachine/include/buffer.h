/*
 * bufer.h
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

#ifndef BUFFER_H
#define BUFFER_H

#include <QByteArray>

class Buffer
{
	
public:
	/// Erstellt einen neuen, leeren Buffer
	Buffer();
	/// Erstellt einen Buffer aus dem angegebenen QByteArray
	Buffer(const QByteArray &buffer);
	
	/// Gibt den modifizierbaren Buffer zurück
	QByteArray& buffer () { return buf; }
	/// Gibt den Buffer zurück
	QByteArray buffer () const { return buf; }
	
	/// Gibt die aktuelle Größe des Buffers zurück
	int size () const { return buf.size(); }
	
	/// Hängt das QByteArray hinten an den Buffer an
	void append (const QByteArray &data);

	/// Gibt die ersten maxlen Bytes zurück und entfernt sie aus dem Buffer
	QByteArray read (int maxlen = 0);
	/// Gibt die erste Zeile zurück (Achtung: nur LF Support) und entfernt sie aus dem Buffer
	QByteArray readLine ();
	
private:
	QByteArray buf;
	
};

#endif // BUFFER_H
