##Sandbox-Maschine

Die Sandbox-Maschine läuft in einer virtuellen Maschine auf dem Worker und verbindet sich auch nur zum Worker. Zur Sicherheit wird ein
Host-only-Netzwerk benutzt. Die Sandbox startet KIs, sobald der Worker dies anfordert, und lädt alles vom Mirror-Server des Workers statt
aus dem FTP-Server auf dem Datastore.
