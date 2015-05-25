/*
 * buildinstructions.h
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

#ifndef BUILDINSTRUCTIONS_H
#define BUILDINSTRUCTIONS_H

#include <QHash>
#include <QString>

class BuildInstructions
{
	
public:
	BuildInstructions();
	
	bool read (const QString &filename, bool verbose = false);
	
	QHash<QString, QString> values () const { return _values; }
	QHash<QString, QString> values (const QString &lang) const { return _subvalues.value(lang); }
	
private:
	QHash<QString, QString> _values;
	QHash<QString, QHash<QString, QString>> _subvalues;
	
};

#endif // BUILDINSTRUCTIONS_H
