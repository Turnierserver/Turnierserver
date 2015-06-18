/*
 * module.h
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

#ifndef MODULE_H
#define MODULE_H

#include <QSettings>
#include <QString>

class Module
{
	
public:
	Module(QSettings *config, QSettings *tmp, const QString &name);
	
	QString name() const { return _name; }
	QString lang() const { return value("Lang").toString(); }
	QString build() const  { return value("Build").toString(); }
	QString folder() const  { return value("Folder").toString(); }
	QStringList dependencies() const  { return value("Dependency").toStringList(); }
	
	int build(const QString &currentHash);
	
protected:
	QVariant value(const QString &key) const;
	
private:
	QSettings *_config, *_tmp;
	QString _name;
	
};

#endif // MODULE_H
