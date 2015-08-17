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
#include "upload.h"

#include <error.h>
#include <stdio.h>
#include <unistd.h>

#include <QDebug>
#include <QDir>

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
	int __retval = (retval); \
	_tmp->setValue(currentHash + "/" + name() + "_state", "build"); \
	_tmp->setValue(currentHash + "/" + name() + "_ret", __retval); \
	return __retval;
	
	
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
	else if (lang() == "Cpp")
	{
		if (build() == "QMake")
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
            RETURN_BUILD(system("qmake -makefile && make -j2"))
		}
		ELSE_UNKNOWN
	}
	else if (lang() == "Python")
	{
		if (build() == "")
			return 0;
		ELSE_UNKNOWN
	}
	ELSE_UNKNOWN
			
#undef ELSE_UNKNOWN
#undef RETURN_ERROR
#undef RETURN_BUILD
}

int Module::start()
{
	// die config bei bedarf ins verzeichnis kopieren
	QFileInfo configFile(QDir(_tmp->value("ConfigRepoClonePath").toString()).absoluteFilePath(_config->value(name() + "/Config", "_n_o_n_e_x_i_s_t_e_n_t").toString()));
	if (configFile.exists())
	{
		QString target = QDir(QDir(_tmp->value("RepoClonePath").toString()).absoluteFilePath(_config->value(name() + "/Folder").toString())).absoluteFilePath(configFile.fileName());
		qDebug() << "copy" << configFile.absoluteFilePath() << "to" << target;
		QFile::remove(target);
		if (!QFile::copy(configFile.absoluteFilePath(), target))
			fprintf(stderr, "Konnte Konfigurationsdatei %s nicht kopieren!\n", qPrintable(configFile.fileName()));
	}
	
#define ELSE_UNKNOWN \
	else \
	{ \
		fprintf(stderr, "Unknown Language/Build %s/%s\n", qPrintable(lang()), qPrintable(build())); \
		return -1; \
	}
	
	if (lang() == "Java")
	{
		if (build() == "Maven")
		{
			QDir target(_tmp->value("RepoClonePath").toString());
			if (!target.cd(_config->value(name() + "/Folder").toString()) || !target.cd("target"))
			{
				fprintf(stderr, "Konnte nicht ins Verzeichnis des Moduls wechseln\n");
				return 1;
			}
			QString cmd("java -classpath '.");
			for (QString file : target.entryList(QDir::Files))
				if (file.endsWith(".jar"))
					cmd += ":" + target.absoluteFilePath(file);
			if (target.cd("dependency"))
				for (QString file : target.entryList(QDir::Files))
					if (file.endsWith(".jar"))
						cmd += ":" + target.absoluteFilePath(file);
			cmd += "' '" + _config->value(name() + "/MainClass").toString() + "' " + _config->value(name() + "/Args").toString().replace("${CONFIG}", "'" + configFile.absoluteFilePath() + "'");
			printf("%s\n", qPrintable(cmd));
			return system(qPrintable(cmd));
		}
		ELSE_UNKNOWN
	}
	else if (lang() == "Python")
	{
		if (build() == "")
		{
			QDir wd(_tmp->value("RepoClonePath").toString());
			if (!wd.cd(_config->value(name() + "/Folder").toString()))
			{
				fprintf(stderr, "Konnte nicht ins Verzeichnis des Moduls wechseln\n");
				return 1;
			}
			if (chdir(qPrintable(wd.absolutePath())) != 0)
			{
				perror("Konnte nicht ins Verzeichnis des Moduls wechseln");
				return 1;
			}
			QString cmd("python '" + _config->value(name() + "/File").toString() + "' "
						+ _config->value(name() + "/Args").toString().replace("${CONFIG}", "'" + configFile.absoluteFilePath() + "'"));
			printf("%s\n", qPrintable(cmd));
			return system(qPrintable(cmd));
		}
		ELSE_UNKNOWN
	}
	ELSE_UNKNOWN
			
#undef ELSE_UNKNOWN
}

bool Module::upload()
{
#define ELSE_UNKNOWN \
	else \
	{ \
		fprintf(stderr, "Unknown Language/Build %s/%s\n", qPrintable(lang()), qPrintable(build())); \
		return false; \
	}
	
	if (lang() == "Java")
	{
		if (build() == "Maven")
		{
			QDir target(_tmp->value("RepoClonePath").toString());
			if (!target.cd(_config->value(name() + "/Folder").toString()) || !target.cd("target"))
			{
				fprintf(stderr, "Konnte nicht ins Verzeichnis des Moduls wechseln\n");
				return 1;
			}
			return uploadFile(_config, _config->value(name() + "/Upload").toString(), target.absoluteFilePath(_config->value(name() + "/File").toString()));
		}
		ELSE_UNKNOWN
	}
	ELSE_UNKNOWN
	
#undef ELSE_UNKNOWN
}
