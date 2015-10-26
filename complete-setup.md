Packages
========

Diese Paketnamen beziehen sich auf Arch Linux. Auf anderen Distributionen heißen sie vielleicht anders.

- git
- jdk8-openjdk
- maven
- python
- python-pip
- tmux
- isolate-git
- vsftpd oder proftpd oder irgendein ftp server der die lokale Benutzerdatenbank benutzt
- postgresql
- gcc (für python-postgresql-bindings)

Zusätzlich müssen einige Python-Bibliotheken von root installiert werden. Dazu in den `Frontend`-Ordner gehen und

    pip3 install -r requirements.txt

Benutzer
========

Die Benutzung dieser Benutzer wird empfohlen, ist aber nicht zwingend notwendig.

- backend: Zum Ausführen des Backends
- worker: Zum Ausführen des Workers
- sandbox: Zum Ausführen der Sandbox
- frontend: Zum Ausführen des Frontends
- datastore: Als FTP-user, home-Verzeichnis ist der Ort, wo alle Daten des Servers abgelegt werden

PostgreSQL kommt mit einem eigenen Benutzer namens postgres, dieser muss ein Passwort erhalten.

Setup
=====

1. Das Repository klonen

        root:/opt# git clone https://github.com/LuckyLukert/Turnierserver.git

2. Das Java-Netzwerk-Zeugs bauen

        root:/opt/Turnierserver# ./buildNetworkZeugs.sh

3. Die Konfigurationsdateien `turnierserver.prop`, `SandboxManager/sandbox.prop` und `Frontend/_cfg.py` schreiben oder
   auf das Repository (https://github.com/nicosio2/Turnierserver-Config) linken

4. Die [quickstart](https://github.com/LuckyLukert/Turnierserver/blob/master/quickstart)-Datei befolgen was die
   Konfiguration von PostgreSQL- und FTP-Server angeht. Eventuell müssen danach Änderungen an unseren
   Konfigurationsdateien vorgenommen werden.

5. Insofern nicht bereits geschehen

       frontend:/opt/Turnierserver/Frontend$ python3 app.py quickstart

6. Alles starten. Dabei nur ein Backend starten, aber bei Bedarf mehrere Worker (für jeden einen anderen Port verwenden)
   und auf jeden Fall mehrere Sandboxen benutzen.

        backend:/opt/Turnierserver/build$ ./backend.sh ../turnierserver.prop
        worker:/opt/Turnierserver/build$ ./worker.sh ../turnierserver.prop
        sandbox:/opt/Turnierserver/build$ ./sandbox.sh ../SandboxManager/sandbox.prop
        frontend:/opt/Turnierserver/Frontend$ python3 app.py run

