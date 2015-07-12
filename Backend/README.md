##Backend

Server, der für die Koordination der Sandbox-Server und das Starten und Ausführen von Spielen verantwortlich ist.

Sprache: Java

Der Server koordiniert die Worker. Er bekommt Befehle vom Frontend und leitet diese an unbeschäftigte Worker weiter. Sobald ein Worker sich
disconnected wird ein anderer Worker beschäftigt. Sollte das Backend gestoppt werden werden im Verzeichnis `/var/spool/backend` ein paar Dateien
abgelegt, die die aktuell ausgeführten und wartenden Jobs abspeichern. Beim nächsten Start des Backends werden diese Daten wieder eingelesen und
die Aufträge werden (erneut) ausgeführt.

Wichtig: Das Verzeichnis `/var/spool/backend` sollte mit Lese- und Schreibberechtigungen für den das Backend ausführenden Benutzer existieren!
