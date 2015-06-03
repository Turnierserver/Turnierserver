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

bool LangSpec::read(bool verbose, const QString &childLang)
{
	QFile in(":/langs/" + lang());
	if (!in.open(QIODevice::ReadOnly))
	{
		fprintf(stderr, "Kann die Sprachanweisungen für die Sprache %s nicht finden!\n", qPrintable(lang()));
		if (verbose)
			qDebug() << "Fehler beim Öffnen von" << in.fileName() << ":" << in.errorString();
		return false;
	}
	
	variables.insert("LANG", childLang.isEmpty() ? lang() : childLang);
	
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
		if (!_parent->read(verbose, childLang.isEmpty() ? lang() : childLang))
			return false;
		if (verbose)
			qDebug() << "Super-LangSpec gelesen";
	}
	
	// die weiteren Zeilen lesen
	
	for (uint linenum = 2; !(line = in.readLine()).isEmpty(); linenum++)
	{
		if (!evalLine(line, linenum, childLang))
			return false;
	}
	
	in.close();
	return true;
}

bool LangSpec::evalLine(QString line, uint linenum, const QString &childLang)
{
	//qDebug() << "evalLine(" << line << "," << linenum << "," << childLang << ")";
	
	static QRegularExpression varAssignment("^\\s*(?P<name>[a-zA-Z_]+)\\s*(?P<operator>.?=)(?P<value>[^\n\r]*)\\s*$");
	static QRegularExpression absolutePath ("^\\s*absolute\\s+(?P<name>[a-zA-Z_]+)\\s*$");
	static QRegularExpression buildCommand ("^\\s*(?P<name>[a-zA-Z]+):(?P<command>[^\n\r]*)\\s*$");
	static QRegularExpression forLoop      ("^\\s*for\\s+(?P<var>[a-zA-Z_]+)\\s+as\\s+(?P<as>[a-zA-Z_]+)\\s+(?P<code>[^\n\r]+)\\s*$");
	
	line = line.trimmed();
	if (line.isEmpty() || line.startsWith("#"))
		return true;
	
	// Variablenzuweisung
	QRegularExpressionMatch match = varAssignment.match(line);
	if (match.hasMatch())
	{
		QString name = match.captured("name");
		QString value = match.captured("value").trimmed();
		QString op = match.captured("operator");
		
		value = fillVars(value, childLang, linenum);
		
		if (op == "=")
			variables.insert(name, value);
		else if (op == "+=")
			variables.insert(name, string(name, childLang) + " " + value);
		else if (op == "*=")
			variables.insert(name, string(name, childLang) + value);
		else if (op == "?=")
		{
			if (string(name, childLang).isEmpty())
				variables.insert(name, value);
		}
		else
		{
			fprintf(stderr, "%s:%u: Unbekannter Operator %s\n", qPrintable(lang()), linenum, qPrintable(op));
			return false;
		}
		
		return true;
	}
	
	// einen Pfad absolut machen
	match = absolutePath.match(line);
	if (match.hasMatch())
	{
		QString var = match.captured("name");
		QString path = string(var, childLang);
		if (path.isEmpty())
		{
			fprintf(stderr, "%s:%u: Fehler: Variable %s ist leer\n", qPrintable(lang()), linenum, qPrintable(var));
			return false;
		}
		QFileInfo fileInfo(path);
		//if (verbose)
		//	qDebug() << var << ":" << path << "->" << fileInfo.absoluteFilePath();
		variables.insert(var, fileInfo.absoluteFilePath());
		return true;
	}
	
	// einen Befehl hinzufügen
	match = buildCommand.match(line);
	if (match.hasMatch())
	{
		QString name = match.captured("name");
		QString command = match.captured("command").trimmed();
		qDebug() << name << ":" << command; // ohne dieses sysout ist der command iwi komisch
		commands.insert(name, QStringList(commands.value(name)) << command);
		
		return true;
	}
	
	// for-Schleife
	match = forLoop.match(line);
	if (match.hasMatch())
	{
		QString var = match.captured("var");
		QString as = match.captured("as");
		QString code = match.captured("code");
		
		if (!string(as, childLang).isEmpty())
			fprintf(stderr, "Warnung: Die Variable %s wird durch eine for-Schleife überschrieben.\n", qPrintable(as));
		for (QString val : string(var, childLang).split(' ', QString::SkipEmptyParts)) // todo: "" beachten
		{
			//qDebug() << "val in for:" << val;
			evalLine(QString(code).replace("${" + as + "}", val), linenum);
		}
		
		return true;
	}
	
	// ansonsten Syntax-Fehler
	fprintf(stderr, "%s:%d: Syntaxfehler\n", qPrintable(lang()), linenum);
	return false;
}

QString LangSpec::string(const QString &name, const QString &language, bool allowTernary) const
{
	QString key = name;
	//qDebug() << "Searching for" << key << "in lang" << language << "(own language:" << lang() << ")";
	
	// ternary operator parsen
	QRegularExpression ternary("^(?P<name>[a-zA-Z_]+)\\s*\\?\\s*\"(?P<then>[^\"]*)\"\\s*:\\s*\"(?P<else>[^\"]*)\"\\s*$");
	QRegularExpressionMatch match= ternary.match(name);
	if (match.hasMatch())
		key = match.captured("name");
	
	//qDebug() << "key: " << key;
	
	// den Wert der Variable herausfinden
	QString value = variables.value(key);
	if (value.isEmpty())
	{
		if (_parent)
			value = _parent->string(name, language.isEmpty() ? lang() : language, false);
		if (value.isEmpty())
		{
			if (language.isEmpty())
				value = instructions().values(lang()).value(key);
			else
				value = instructions().values(language).value(key);
			if (value.isEmpty())
			{
				value = instructions().values().value(key);
			}
		}
	}
	
	//qDebug() << "value: " << value;
	
	// Wert zurückgeben
	if (match.hasMatch() && allowTernary)
		return (value == "true" ? match.captured("then") : match.captured("else"));
	return value;
}

QString LangSpec::fillVars(const QString &str, const QString &childLang, uint linenum) const
{
	QString value = str; // klonen
	
	while (value.contains('$'))
	{
		int index = value.indexOf('$');
		QString substr = value.mid(index + 2);
		int end = substr.indexOf('}');
		if ((value[index+1] != '{') || (end == -1))
		{
			fprintf(stderr, "%s:%u: Missformed variable\n", qPrintable(lang()), linenum);
			continue;
		}
		substr = substr.mid(0, end);
		value.replace(index, end + 3, string(substr, childLang));
	}
	return value;
}

QStringList LangSpec::targetCommands(const QString &target)
{
	QStringList cmds;
	if (_parent)
		cmds << _parent->targetCommands(target);
	cmds << commands.value(target);
	return cmds;
}
