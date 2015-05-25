/*
 * langspec.h
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

#ifndef LANGSPEC_H
#define LANGSPEC_H

#include <QHash>
#include <QString>
#include <QStringList>

class BuildInstructions;

class LangSpec
{
	Q_DISABLE_COPY(LangSpec) // _parent darf nicht kopiert werden (wegen destruktor)
	
public:
	LangSpec(const BuildInstructions &instructions, const QString &lang);
	~LangSpec();
	
	bool read (bool verbose = false, const QString &childLang = QString());
private:
	bool evalLine (QString line, uint linenum, const QString &childLang = QString());
	
public:
	QString string (const QString &name, const QString &language = QString()) const;
	QString fillVars (const QString &str, const QString &childLang = QString(), uint linenum = 0) const;
	
	QStringList targetCommands (const QString &target);
	
	QString lang () const { return _lang; }
	BuildInstructions instructions () const { return _instructions; }
	
private:
	
	QString _lang;
	BuildInstructions _instructions;
	LangSpec *_parent = 0;
	QHash<QString, QStringList> commands;
	QHash<QString, QString> variables;
	
};

#endif // LANGSPEC_H
