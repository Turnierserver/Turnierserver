/*
 * buffer.cpp
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

#include "buffer.h"

Buffer::Buffer ()
{
}

Buffer::Buffer (const QByteArray &buffer)
	: buf(buffer)
{
}

void Buffer::append (const QByteArray &data)
{
	buf.append(data);
}

QByteArray Buffer::read (int maxlen)
{
	if (maxlen < 0) // wichtig für readLine()
		return QByteArray();
	if (maxlen == 0 || (maxlen >= buf.size()))
	{
		QByteArray buffer = buf;
		buf = QByteArray();
		return buffer;
	}
	
	QByteArray buffer = buf.mid(0, maxlen);
	buf = buf.mid(maxlen);
	return buffer;
}

QByteArray Buffer::readLine ()
{
	return read(buf.indexOf('\n'));
}
