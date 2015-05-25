/*
 * buildinstructions.cpp
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

#include "buildinstructions.h"

#include <stdio.h>

#include <QDebug>
#include <QFile>
#include <QRegularExpression>

BuildInstructions::BuildInstructions()
{
}

bool BuildInstructions::read(const QString &filename, bool verbose)
{
	QFile in(filename);
	if (!in.open(QIODevice::ReadOnly))
	{
		fprintf(stderr, "Kann die Bauanweisungen nicht aus %s lesen: %s\n", qPrintable(filename), qPrintable(in.errorString()));
		return false;
	}
	
	QString currentLang;
	
	QRegularExpression regex("^\\s*(?P<name>[a-zA-Z_]+)\\s*(?P<operator>.?=)(?P<value>[^\n\r]*)\\s*$");
	QByteArray line;
	for (uint linenum = 1; !(line = in.readLine()).isEmpty(); linenum++)
	{
		line = line.trimmed();
		if (line.isEmpty() || line.startsWith("#"))
			continue;
		
		if (line == "}")
		{
			if (verbose)
				qDebug() << "Verlasse Sprache" << currentLang;
			currentLang = QString();
			continue;
		}
		if (line.endsWith("{"))
		{
			if (!currentLang.isEmpty())
			{
				fprintf(stderr, "%s:%u: Kann nicht zwei Sprachen gleichzeitig bauen\n", qPrintable(filename), linenum);
				return false;
			}
			currentLang = line.mid(0, line.length() - 1).trimmed();
			if (verbose)
				qDebug() << "Betrete Sprache" << currentLang;
			continue;
		}
		
		QRegularExpressionMatch match = regex.match(line);
		if (!match.hasMatch())
		{
			fprintf(stderr, "%s:%u: Syntaxfehler\n", qPrintable(filename), linenum);
			return false;
		}
		
		QString name  = match.captured("name");
		QString op    = match.captured("operator");
		QString value = match.captured("value").trimmed();
		if (currentLang.isEmpty())
		{
			if (op == "=")
				_values.insert(name, value);
			else if (op == "+=")
				_values.insert(name, _values.value(name) + " " + value);
			else
			{
				fprintf(stderr, "%s:%u: Unknown operator %s\n", qPrintable(filename), linenum, qPrintable(op));
				return false;
			}
		}
		else
		{
			QHash<QString, QString> map = _subvalues.value(currentLang);
			if (op == "=")
				map.insert(name, value);
			else if (op == "+=")
				map.insert(name, map.value(name) + " " + value);
			else
			{
				fprintf(stderr, "%s:%u: Unknown operator %s\n", qPrintable(filename), linenum, qPrintable(op));
				return false;
			}
			_subvalues.insert(currentLang, map);
		}
	}
	
	in.close();
	return true;
}
