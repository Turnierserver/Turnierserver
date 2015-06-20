/*
 * patcher.h
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

#ifndef PATCHER_H
#define PATCHER_H

#include "module.h"

#include <sys/types.h>

#include <QDir>
#include <QObject>
#include <QSet>
#include <QSettings>
#include <QTemporaryDir>
#include <QTemporaryFile>

#include <qithubbranch.h>
#include <qithubrepository.h>

class Patcher : public QObject
{
	Q_OBJECT
	
public:
	Patcher(QSettings *config, const QitHubRepository &repo, const QString &repoBranch, const QitHubRepository &configRepo, const QString &configBranch, QObject *parent = 0);
	
public slots:
	void startFrontend();
	void startBackend();
	void startCodr();
	void startWorker();
	
	void update();
	
protected:
	/// Startet das Modul in einem eigenen Prozess und gibt die PID zurück.
	virtual pid_t start (Module &module);
	/// Sendet einen kill-Befehl an den Prozess.
	virtual bool stop(pid_t process);
	
private:
	QSettings *_config, *_tmp;
	QTemporaryFile tmpConfig;
	
	QitHubRepository repo, config;
	QitHubBranch repoBranch, configBranch;
	QString latestRepoCommit, latestConfigCommit;
	
	QTemporaryDir repoTmpDir, configTmpDir;
	QDir repoPath, configPath;
	
	QSet<QString> _modules;
	pid_t frontend = 0, backend = 0, worker = 0;
	
};

#endif // PATCHER_H
