/*
 * module.cpp
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

#include "module.h"

#include <error.h>
#include <stdio.h>
#include <unistd.h>

Module::Module(QSettings *config, QSettings *tmp, const QString &name)
	: _config(config)
	, _tmp(tmp)
	, _name(name)
{
}

QVariant Module::value(const QString &key) const
{
	return _config->value(name() + "/" + key);
}

int Module::build(const QString &currentHash)
{
	QString state = _tmp->value(currentHash + "/" + name() + "_state", "not build").toString();
	if (state == "not modified")
		return build(_tmp->value(currentHash + "/" + "Parent").toString());
	else if (state == "build")
		return _tmp->value(currentHash + "/" + name() + "_ret").toInt();
	else if ((state != "unknown") && (state != "not build") && (state != "modified") && (state != "error"))
		fprintf(stderr, "WARNUNG: Unbekannter Zustand %s\n", qPrintable(state));
	
	for (QString dependency : dependencies())
	{
		if (dependency.isEmpty())
			continue;
		Module module(_config, _tmp, dependency);
		int ret = module.build(currentHash);
		if (ret != 0)
			return ret;
	}
	
	printf("Baue Modul %s (%s/%s)\n", qPrintable(name()), qPrintable(lang()), qPrintable(build()));
	
#define ELSE_UNKNOWN \
	else\
	{ \
		fprintf(stderr, "Unknown Language/Build %s/%s\n", qPrintable(lang()), qPrintable(build())); \
		_tmp->setValue(currentHash + "/" + name() + "_state", "unknown"); \
		_tmp->setValue(currentHash + "/" + name() + "_ret", -1); \
		return -1; \
	}
	
#define RETURN_ERROR \
	_tmp->setValue(currentHash + "/" + name() + "_state", "error"); \
	_tmp->setValue(currentHash + "/" + name() + "_ret", 1); \
	return 1;
	
#define RETURN_BUILD(retval) \
	_tmp->setValue(currentHash + "/" + name() + "_state", "build"); \
	_tmp->setValue(currentHash + "/" + name() + "_ret", (retval)); \
	return (retval);
	
	
	if (lang() == "Java")
	{
		if (build() == "Maven")
		{
			if (chdir(_tmp->value("RepoClonePath").toByteArray()) != 0)
			{
				perror("Konnte nicht ins Verzeichnis des geklonten Repositories wechseln");
				RETURN_ERROR
			}
			if (chdir(_config->value(name() + "/Folder").toByteArray()) != 0)
			{
				perror("Konnte nicht ins Verzeichnis des Moduls wechseln");
				RETURN_ERROR
			}
			RETURN_BUILD(system("mvn clean package install dependency:copy-dependencies"))
		}
		ELSE_UNKNOWN
	}
	ELSE_UNKNOWN
}
