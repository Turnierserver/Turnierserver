#!/bin/bash

export $(cat "$HOME/.backup.txt")

bsdtar cfjv "$backupDir/backup-$(date '+%F')-ftp.tar.bz2" "$ftpDir"
mysqldump -u "$mysqlUser" -p"$mysqlPassword" -h "$mysqlHost" --all-databases >"$backupDir/backup-$(date '+%F')-mysql.sql"
