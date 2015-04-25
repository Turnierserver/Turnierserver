lokales Programm, das auf dem Computer des Teilnehmers laeuft
Sprache: Java
Unterscheidet zwischen ONLINE-Modus und OFFLINE-Modus

Ordnerstruktur:
Spieler-Ordner: enthält für jeden Spieler einen Ordner mit dem Namen "SpielerID.txt", der den Quellcode und die kompilierte KI hat.
                jeder Spieler-Ordner enthält noch Unterordner für die einzelnen Versionen
gespielte-Spiele-Ordner: enthält Spiel-Dateien (betitelt mit "SpielID.txt")


Die grundsätzlichen Funktionen sind:
- KI-Verwaltung: verwaltet KIs; enthält den Editor (im Online-Modus Möglichkeit zum hochladen)
- Spiel-Tab: führt Spiel mit bestimmten KIs aus (im Onlinemodus sind auch die vom Server verfügbar)
- Rangliste: kann nur im Onlinemodus angeschaut werden (evtl. nur HTML-Viewer für die Webseite)
- Anleitung-Tab: automatischer Datei-Download + Anmeldung + Erklärungen



OFFLINE-Modus:
Voraussetzung ist, dass der Spieler schon die Spiellogik und die Libs runtergeladen hat
Wenn man zum Internet verbunden ist, kann beides (über den Anleitungs-Tab) automatisch runtergeladen werden

Nur Offline-Spieler werden im Spiel-Tab angezeigt

Bei Spielstart:
Offline-Backend wird gestartet (kann nur ein Spiel gleichzeitig spielen)
Frontend (also die eigentliche ESU) generiert eine Spiel-ID legt eine Spiel-Datei an
  sendet dem Backend die SpielID
Offline-Backend liest die Spiel-Datei aus
  darin stehen die SpielerIDs der KIs, die (wenn noch nicht geschehen) kompiliert werden
  startet die KIs
  kommuniziert mit den KIs (leitet sie mit der Logik)
  bei Spielende werden die Spielzüge in die Spiel-Datei geschrieben und dem Frontend die Fertigstellung gemeldet
Offline-Backend wird beendet



ONLINE-Modus:
Der Nutzer ist mit Nutzernamen und Passwort beim Turnierserver angemeldet.

KI-Verwaltung hat die Option des Hochladens

Spiel-Tab unterscheidet je nach KI-Auswahl zwischen Offline-Spiel und Online-Spiel
Wenn die ausgewählte KI noch nicht hochgeladen wurde, wird gefragt, ob sie automatisch hochgeladen werden soll

Bei einem Online-Spiel wird nur der Befehl, der die zwei SpielerIDs enthaelt, an den Server gesendet

Anzeige der Rangliste











