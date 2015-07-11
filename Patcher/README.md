##Patcher

Der Patcher hat die Aufgabe, das System zu starten und Updates einzuspielen. Der Patcher wird mit einem Shell-Skript gestartet, dass den Patcher
immer wieder neu startet, sobald sich dieser mit dem Statuscode 0 beendet. Das ist wichtig, damit sich der Patcher auch selber patchen kann. Zur
GitHub-Integration hat der Patcher seine eigene Bibliothek für das GitHub-API namens QitHubAPI. Sie baut auf Qt5 auf. Ein git-Klient auf der
Kommandozeile ist nicht nötig.

Der Patcher ist in C++ geschrieben und erforder Qt5Core, Qt5Network und libarchive zum kompilieren.
