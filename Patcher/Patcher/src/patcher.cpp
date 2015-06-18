/*
 * patcher.cpp
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
#include "patcher.h"

#include <stdio.h>
#include <signal.h>
#include <unistd.h>

#include <archive.h>
#include <archive_entry.h>

#include <QCoreApplication>
#include <QDebug>
#include <QDir>
#include <QThread>

QString download (const QDir &tmpPath, const QitHubBranch &branch)
{
	QString repoTarball = tmpPath.filePath("repo.tar.gz");
	branch.download(repoTarball);
	
	archive *a = archive_read_new();
	archive_read_support_filter_all(a);
	archive_read_support_format_all(a);
	int r = archive_read_open_filename(a, qPrintable(repoTarball), 10240);
	if (r != ARCHIVE_OK)
	{
		return QString();
	}
	
	QString path;
	archive_entry *entry;
	while (archive_read_next_header(a, &entry) == ARCHIVE_OK)
	{
		if (path.isEmpty() && (archive_entry_filetype(entry) == AE_IFDIR))
		{
			path = archive_entry_pathname(entry);
			path = path.mid(0, path.indexOf('/'));
			path = tmpPath.absoluteFilePath(path);
			archive_read_data_skip(a);
		}
		
		switch (archive_entry_filetype(entry))
		{
		case AE_IFDIR:
			if (!tmpPath.mkdir(archive_entry_pathname(entry)))
			{
				fprintf(stderr, "Konnte Verzeichnis %s nicht anlegen.\n", qPrintable(tmpPath.filePath(archive_entry_pathname(entry))));
				return QString();
			}
			break;
		case AE_IFLNK:
			if (!QFile::link(archive_entry_symlink(entry), tmpPath.filePath(archive_entry_pathname(entry))))
			{
				fprintf(stderr, "Konte symbolischen Link %s nicht anlegen.\n", qPrintable(tmpPath.filePath(archive_entry_pathname(entry))));
				return QString();
			}
			break;
		case AE_IFREG:
		{
			QFile out(tmpPath.filePath(archive_entry_pathname(entry)));
			if (!out.open(QIODevice::WriteOnly))
			{
				fprintf(stderr, "Kann Datei %s nicht öffnen: %s\n", qPrintable(out.fileName()), qPrintable(out.errorString()));
				return QString();
			}
			
			size_t size;
			size_t bufsize = 8192;
			char *buf = new char[bufsize];
			while ((size = archive_read_data(a, buf, bufsize)) > 0)
				out.write(buf, size);
			
			out.close();
			break;
		}
		default:
			fprintf(stderr, "%s:%d: Unbekannter filetype %d\n", __FILE__, __LINE__, archive_entry_filetype(entry));
		}
	}
	
	archive_read_free(a);
	return path;
}

Patcher::Patcher(QSettings *config, const QitHubRepository &repo, const QString &repoBranch, const QitHubRepository &configRepo, const QString &configBranch, QObject *parent)
	: QObject(parent)
	, _config(config)
	, repo(repo)
	, config(configRepo)
	, repoBranch(this->repo.client(), this->repo, repoBranch)
	, configBranch(this->config.client(), this->config, configBranch)
{
	repoPath = download(repoTmpDir.path(), this->repoBranch);
	configPath = download(configTmpDir.path(), this->configBranch);
	if (!repoPath.exists() || !configPath.exists())
	{
		fprintf(stderr, "Fehler beim Herunterladen der benötigten Repositories.\n");
		QCoreApplication::exit(1);
		return;
	}
	
	if (tmpConfig.open())
		tmpConfig.close();
	qDebug() << tmpConfig.fileName();
	_tmp = new QSettings(tmpConfig.fileName(), QSettings::IniFormat);
	_tmp->setValue("RepoClonePath", repoPath.absolutePath());
	_tmp->setValue("ConfigRepoClonePath", configPath.absolutePath());
}

void Patcher::startBackend()
{
	static Module module(_config, _tmp, "Backend");
	int ret = module.build(repoBranch.latestCommit().sha());
	if (ret != 0)
		exit(ret);
}

void Patcher::startWorker()
{
	static Module module(_config, _tmp, "Worker");
	int ret = module.build(repoBranch.latestCommit().sha());
	if (ret != 0)
		exit(ret);
}

void Patcher::startFrontend()
{
	QDir frontendDir(repoPath);
	frontendDir.cd("Frontend");
	frontendDir.remove("_cfg.py");
	QFile::copy(configPath.absoluteFilePath("Frontend/_cfg.py"), frontendDir.absoluteFilePath("_cfg.py"));
	frontend = start(frontendDir.absolutePath(), "python3 app.py run");
}

pid_t Patcher::start (const QString &wd, const QString &cmd)
{
	pid_t pid = fork();
	if (pid == 0) // geforktes programm
	{
		if (chdir(qPrintable(wd)) != 0)
		{
			perror("Fehler beim Wechseln des Verzeichnises");
			exit(1);
		}
		printf("%s$ %s\n", qPrintable(wd), qPrintable(cmd));
		int ret = system(qPrintable(cmd));
		printf("%d\t%s\n", ret, qPrintable(cmd));
	}
	return pid;
}

bool Patcher::stop (pid_t process)
{
	if (kill(process, SIGTERM) != 0)
	{
		if (errno == ESRCH)
			return true;
		else
		{
			perror("Kann Prozess nicht beenden");
			return false;
		}
	}
	QThread::sleep(1000);
	if (kill(process, SIGKILL) != 0)
	{
		if (errno == ESRCH)
			return true;
		else
		{
			perror("Kann Prozess nicht töten");
			return false;
		}
	}
	return true;
}
