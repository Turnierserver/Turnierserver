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

#include <qithubfile.h>

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
	, latestRepoCommit(this->repoBranch.latestCommit().sha())
	, latestConfigCommit(this->configBranch.latestCommit().sha())
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
	_modules << "Backend";
	static Module module(_config, _tmp, "Backend");
	int ret = module.build(latestRepoCommit);
	if (ret != 0)
		exit(ret);
    uids["Backend"] = start(module);
}

void Patcher::startWorker(uint sandboxes)
{
	_modules << "Worker";
	static Module module(_config, _tmp, "Worker");
	int ret = module.build(latestRepoCommit);
	if (ret != 0)
		exit(ret);
    uids["Worker"] = start(module);
	
	for (uint i = 0; i < sandboxes; i++)
		startSandbox(i);
}

void Patcher::startSandbox(uint number)
{
	system(qPrintable(QString("VBoxManage controlvm patcher-sandbox-" + QString::number(number) + " poweroff")));
	system(qPrintable(QString(_tmp->value("RepoClonePath").toString() + "/Sandbox/VM/startVM.sh patcher-sandbox-" + QString::number(number)
							  + " Sandbox/ logs/worker0/sandbox" + QString::number(number))));
}

void Patcher::startFrontend()
{
	_modules << "Frontend";
	static Module module(_config, _tmp, "Frontend");
	int ret = module.build(latestRepoCommit);
	if (ret != 0)
		exit(ret);
    uids["Frontend"] = start(module);
}

void Patcher::startCodr()
{
	_modules << "Codr";
	static Module module(_config, _tmp, "Codr");
	int ret = module.build(latestRepoCommit);
	if (ret != 0)
	{
		fprintf(stderr, "Fehler: Der Aufruf von build() für Codr hat %d zurückgegeben.\n", ret);
		return;
	}
	// sonderrolle: der codr wird hochgeladen statt ausgeführt
	module.upload();
}

void Patcher::update()
{
	printf("%s:%d: Patcher::update called\n", __FILE__, __LINE__);
	repoBranch.update();
	configBranch.update();
	
    QFile commit(repoPath.absoluteFilePath("commit.txt"));
    if(!commit.exists()) {
        commit.open(QIODevice::WriteOnly);
        commit.write(repoBranch.latestCommit().sha().toUtf8());
        commit.close();
    }

    commit.open(QIODevice::ReadOnly);
    QByteArray line;
    while(!(line = commit.readLine()).isEmpty()) {}
    commit.close();

    QList<QitHubCommit> commits = repoBranch.commitsSince(QString::fromUtf8(line));
	QSet<QString> modifiedDirs;
    for (QitHubCommit commit : commits)
	{
		for (QitHubFile file : commit.modifiedFiles())
		{
			QFile out(repoPath.absoluteFilePath(file.filename()));
			if (QFileInfo(out).isSymLink())
				continue;
			if (!out.open(QIODevice::WriteOnly))
			{
				fprintf(stderr, "%s:%d: Fehler beim Öffnen von %s: %s\n", __FILE__, __LINE__, qPrintable(out.fileName()), qPrintable(out.errorString()));
				exit(0); // der Fehler sollte beim komplett neuen klonen behoben sein
			}
			out.write(file.content());
			out.close();
			
			modifiedDirs << file.filename().mid(0, qMax(0, file.filename().indexOf('/')));
		}
	}

    commit.open(QIODevice::WriteOnly);
    commit.write(repoBranch.latestCommit().sha().toUtf8());
    commit.close();
	
    QFile commitConfig(configPath.absoluteFilePath("commit.txt"));
    if(!commitConfig.exists()) {
        commitConfig.open(QIODevice::WriteOnly);
        commitConfig.write(configBranch.latestCommit().sha().toUtf8());
        commitConfig.close();
    }

    commitConfig.open(QIODevice::ReadOnly);
    QByteArray commitLine;
    while(!(line = commitConfig.readLine()).isEmpty()) {}
    commitConfig.close();

    commits = configBranch.commitsSince(QString::fromUtf8(commitLine));
	QSet<QString> modifiedFiles;
	for (QitHubCommit commit : commits)
	{
		for (QitHubFile file : commit.modifiedFiles())
		{
			QFile out(configPath.absoluteFilePath(file.filename()));
			if (QFileInfo(out).isSymLink())
				continue;
			if (!out.open(QIODevice::WriteOnly))
			{
				fprintf(stderr, "%s:%d: Fehler beim Öffnen von %s: %s\n", __FILE__, __LINE__, qPrintable(out.fileName()), qPrintable(out.errorString()));
				exit(0); // der Fehler sollte beim komplett neuen klonen behoben sein
			}
			out.write(file.content());
			out.close();
			
			modifiedFiles << file.filename();
		}
	}
	
    commitConfig.open(QIODevice::WriteOnly);
    commitConfig.write(configBranch.latestCommit().sha().toUtf8());
    commitConfig.close();

    if (modifiedDirs.contains("Patcher") || modifiedFiles.contains("Patcher.ini")) {
        for(QString module : _modules) {
            if(uids.contains(module)) {
                stop(uids[module]);
            }
        }
        exit(0);
    }
	
	for (QString module : _modules)
    {
        if(module == "Frontend")
            continue;
        bool restart = modifiedDirs.contains(_config->value(module + "/Folder").toString()) || modifiedFiles.contains(_config->value(module + "/Config").toString()) || containsDependency(modifiedFiles, _config->value(module + "/Dependency").toStringList());

        if (restart) {
            if(uids.contains(module))
                stop(uids[module]);
            if(module == "Backend")
                startBackend();
            else if(module == "Worker")
                startWorker();
            else if(module == "Codr")
                startCodr();
        }
	}
}

bool Patcher::containsDependency(QSet<QString> list, QStringList dependencies) {
    for(QString dependency : dependencies) {
        if(!list.contains(_config->value(dependency + "/Folder").toString()) && !containsDependency(list, _config->value(dependency + "/Dependency").toStringList())) {
            return false;
        }
    }
    return true;
}

pid_t Patcher::start (Module &module)
{
	pid_t pid = fork();
	if (pid == 0) // geforktes programm
	{
		int ret = module.start();
		printf("%s exited with exit code %d\n", qPrintable(module.name()), ret);
		exit(ret);
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
