/*
 * langspec.cpp
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
#include "langspec.h"

#include <stdio.h>

#include <QDebug>
#include <QFile>
#include <QFileInfo>
#include <QRegularExpression>

LangSpec::LangSpec(const BuildInstructions &instructions, const QString &lang)
	: _lang(lang)
	, _instructions(instructions)
{
}

LangSpec::~LangSpec()
{
	if (_parent)
		delete _parent;
}

bool LangSpec::read(bool verbose)
{
	QFile in(":/langs/" + lang());
	if (!in.open(QIODevice::ReadOnly))
	{
		fprintf(stderr, "Kann die Sprachanweisungen für die Sprache %s nicht finden!\n", qPrintable(lang()));
		if (verbose)
			qDebug() << "Fehler beim Öffnen von" << in.fileName() << ":" << in.errorString();
		return false;
	}
	
	// die erste Zeile enthält den extends-Befehl
	QByteArray line = in.readLine().trimmed();
	QRegularExpression regex("extends ([a-z]+)");
	QRegularExpressionMatch match = regex.match(line);
	if (!match.hasMatch())
	{
		if (line.startsWith("extends"))
			fprintf(stderr, "%s:1: Unerlaubte Zeichen nach dem extends-Befehl\n", qPrintable(lang()));
		else
			fprintf(stderr, "%s:1: Der extends-Befehl fehlt\n", qPrintable(lang()));
		return false;
	}
	QString extends = match.captured(1);
	if (extends != "nothing")
	{
		if (verbose)
			qDebug() << "Lese Super-LangSpec " << extends;
		_parent = new LangSpec(instructions(), extends);
		if (!_parent->read(verbose))
			return false;
		if (verbose)
			qDebug() << "Super-LangSpec gelesen";
	}
	
	// die weiteren Zeilen lesen
	QRegularExpression varAssignment("^\\s*(?P<name>[a-zA-Z_]+)\\s*(?P<operator>.?=)(?P<value>[^\n\r]*)\\s*$");
	QRegularExpression absolutePath ("^\\s*absolute (?P<name>[a-zA-Z_]+)\\s*$");
	QRegularExpression buildCommand ("^\\s*(?P<name>[a-zA-Z]+):(?P<command>[^\n\r]*)\\s*$");
	for (uint linenum = 2; !(line = in.readLine()).isEmpty(); linenum++)
	{
		line = line.trimmed();
		if (line.isEmpty() || line.startsWith("#"))
			continue;
		
		// Variablenzuweisung
		QRegularExpressionMatch match = varAssignment.match(line);
		if (match.hasMatch())
		{
			QString name = match.captured("name");
			QString value = match.captured("value").trimmed();
			QString op = match.captured("operator");
			
			while (value.contains('$'))
			{
				int index = value.indexOf('$');
				QString substr = value.mid(index + 2);
				int end = substr.indexOf('}');
				if ((value[index+1] != '{') || (end == -1))
				{
					fprintf(stderr, "%s:%u: Missformed variable\n", qPrintable(lang()), linenum);
					return false;
				}
				substr = substr.mid(0, end);
				value.replace(index, end + 3, string(substr));
			}
			
			if (op == "=")
				variables.insert(name, value);
			else if (op == "+=")
				variables.insert(name, string(name) + " " + value);
			else if (op == "?=")
			{
				if (string(name).isEmpty())
					variables.insert(name, value);
			}
			else
			{
				fprintf(stderr, "%s:%u: Unbekannter Operator %s\n", qPrintable(lang()), linenum, qPrintable(op));
				return false;
			}
			
			continue;
		}
		
		// einen Pfad absolut machen
		match = absolutePath.match(line);
		if (match.hasMatch())
		{
			QString var = match.captured("name");
			QString path = string(var);
			if (path.isEmpty())
			{
				fprintf(stderr, "%s:%u: Fehler: Variable %s ist leer\n", qPrintable(lang()), linenum, qPrintable(var));
				return false;
			}
			QFileInfo fileInfo(path);
			if (verbose)
				qDebug() << var << ":" << path << "->" << fileInfo.absoluteFilePath();
			variables.insert(var, fileInfo.absoluteFilePath());
			continue;
		}
		
		// einen Befehl hinzufügen
		match = buildCommand.match(line);
		if (match.hasMatch())
		{
			continue;
		}
		
		// ansonsten Syntax-Fehler
		fprintf(stderr, "%s:%d: Syntaxfehler\n", qPrintable(lang()), linenum);
		return false;
	}
	
	in.close();
	return true;
}

QString LangSpec::string(const QString &name) const
{
	QString key = name;
	
	// ternary operator parsen
	QRegularExpression ternary("^(?P<name>[a-zA-Z_]+)\\s*\\?\\s*\"(?P<then>[^\"]*)\"\\s*:\\s*\"(?P<else>[^\"]*)\"\\s*$");
	QRegularExpressionMatch match= ternary.match(name);
	if (match.hasMatch())
		key = match.captured("name");
	
	// den Wert der Variable herausfinden
	QString value = variables.value(key);
	if (value.isEmpty())
	{
		if (_parent)
			value = _parent->string(name);
		else
		{
			value = instructions().values(lang()).value(key);
			if (value.isEmpty())
			{
				value = instructions().values().value(key);
			}
		}
	}
	
	// Wert zurückgeben
	if (match.hasMatch())
		return (value == "true" ? match.captured("then") : match.captured("else"));
	return value;
}
