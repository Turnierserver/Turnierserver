Aktuelle Infos
=======
Dies ist eine Beta-Version eines neuen Turnierservers. Wir arbeiten ständig daran diesen für euch zu verbessern. Meldet euch bei Anregungen bitte an [Email hier einfügen](mailto:email@hier.einfuegen).


Anleitung
=======
Erstellen einer KI
-------
Eine KI kann in deinem Profil erstellt werden. Dieses erreichst du, in dem du dich anmeldest und auf deinen Benutzernamen links neben dem Einstellungsmenu klickst.

Hier hast du nun die Möglichkeit, deine schon existierenden KIs zu bearbeiten, sowie deren öffentliches Profil zu besuchen und eine neue KI zu erstellen.

Nach dem du auf den Knopf `KI erstellen` geklickt hast, solltest du zuerst einmal die Standarteinstellungen ändern:

* Im Feld `Namen` kannst du den Namen der KI eintragen. Dieser muss nicht eindeutig sein, da deine KI an ihrer ID erkannt wird. Bitte erstelle keine KI mit Namen, welche deine Mitstreiter als negativ auffassen könnten, oder die ein Teil der Lösung der Aufgabe verraten, da wir uns sonst gezwungen sehen, diese KI, und bei mehrmaligem Wiederholen, deinen Account zu löschen.
* Im Feld `Sprache` kannst du aus einem Dropdown Menü die Sprache deiner KI auswählen. Momentan werden nur Java und Python unterstützt, wir arbeiten jedoch hart daran, euch weitere zur Verfügung zu stellen.
* Im Feld `Beschreibung` könnt ihr eurer KI eine öffentlich sichtbare Beschreibung geben. Hierfür gelten die selben Regeln wie für den Namen.
* Im Feld `Extras` könnt ihr die Bibliotheken auswählen, die eure KI verwendet. Dies könnt ihr auch in der Datei libraries.txt, welche im root-Verzeichnis in eurer KI liegen muss, aber hierzu später mehr.
* Du kannst wenn du auf das Icon der KI klickst dieses ändern. Hier bitte nur SFW content hochladen, sonst kann euer Account permanent gelöscht werden.

Klicke auf den Knopf `Speichern` wenn du fertig mit deinen Änderungen bist.

Wir bieten euch mehrere Arten, wie ihr den Code eurer KI bearbeiten könnt. Zum einen gibt es den Knopf `SimplePlayer kopieren`. Dieser kopiert die Qualifikations-KI in eure KI hinein, hiermit habt ihr schon einmal eine funktionierende KI, auf welcher ihr eure aufbauen könnt. Des weiteren gibt es die folgenden 2 Möglichkeiten:

* Mit dem Knopf `Code bearbeiten` kommt ihr in die Ordnerstruktur eurer KI. Hier könnt ihr den Code eurer KI anschauen und mit unserem Editor bearbeiten. Ihr könnt auch Ordner und Dateien erstellen.
* Mit den Knopf `ZIP hochladen` in der Versionstabelle könnt ihr eure KI in einem ZIP-Archiv hochladen.

Wenn du nun deine KI nach belieben bearbeitet hast, wird es Zeit, diese zu kompilieren. Hierzu drückst du in der Versionstabelle den Knopf `Kompilieren`. Wenn du nun auf der geladenen Seite den Knopf `Kompilierung anfragen` drückst, wird diese gestartet. Du kannst den Output dieser sehen. Wenn der Kompilierungsprozess erfolgreich abgeschlossen wude, erscheint neben dem `Kompilierung anfragen` Knopf und in der Versionstabelle ein neuer Knopf: `Qualifizieren`.

Dieser Vorgang sollte von der alten Plattform noch bekannt sein. Für die, die zum ersten mal am Bundeswettbewerb Informatik auf der Turnierplattform teilnehmen: Damit deine KI gegen KIs anderer Nutzer antreten darf, muss sie sich erst einmal gegen eine sehr einfache KI beweisen, ohne abzustürzen oder in Zeitnot zu geraten. Dieses Qualifikationsspiel wird mit einem klick auf den Knopf `Qualifizieren` gestartet. Du wirst nun auf die Seite der Qualifikation geführt, auf der du die genauen Details in Echtzeit verfolgen kannst. Sobald diese abgeschlossen ist musst du entweder deine KI neu überarbeiten, Kompilieren, und Qualifizieren (wenn die Qualifikation fehlgeschlagen ist), oder du kannst in der Versionstabelle der KI auf den Knopf `Freigeben` klicken. Dies hält dich davon ab deine KI wieder zu kompilieren, bis du eine neue Version erstellt hast. Des weiteren gibt es deine Version für den Wettkampf mit anderen KIs frei. Wenn du nun eine neue Version ersellst kannst du auch den Quellcode deiner AI in der Versionstabelle herunterladen.


Minimale KI Struktur
====================

In jede KI gehören minimal 3 Dateien. Diese werden wir dir hier erklären.

Die erste Datei heißt `libraries.txt` und enthält alle Bibliotheken, welche zur Kompilierung und Ausführung deiner KI benötigt werden. Dazu musst du nur den Namen einer Bibliothek und die gewünschte Version mit einem '/' in eine Zeile schreiben. Im Falle von der neuesten Lombok-Version wäre dies "lombok/1.16.4". Momentan gibt es folgende Bibliotheken für folgende Sprachen:

**Java:**

- [lombok](https://projectlombok.org/) version 1.16.4
- [xtend](http://eclipse.org/xtend/) version 2.8.3
- [xtext](https://eclipse.org/xtext/) version 2.8.3 (wird von xtend benötigt)

**Python:**

Momentan stellen wir noch keine Python Bibliotheken zur Verfügung. Schau weiter unten nach, wie du uns Bibliotheken vorschlagen kannst.

Sende einfach eine Email mit dem Namen, der Sprache und der Version der Bibliothek und wieso diese nützlich für die Benutzer dieser Plattform ist an
[Email hier einfügen](mailto:email@hier.einfuegen), um uns diese Vorzuschlagen.

----------------------------------------------------------

Die zweite Datei heißt `settings.prop` und enthält verschiedene Sprachspezifische Informationen:

**Java:**

Bei einer Java KI muss einfach folgendes in die erste Zeile geschrieben werden:
```
mainclass=package.repräsentation.der.klasse.mit.main.methode.AI
```
Die Klasse kann hierbei einen beliebigen Namen und Pfad haben.

**Python:**

Bei einer Python AI muss einfach folgendes in die erste Zeile geschrieben werden:
```
filename=name_der_datei.py
```
Die Datei kann hierbei einen beliebigen Namen und Pfad haben. Bei Ordnern sollte auf die Paket-Importregeln geachet werden. (. statt /)

----------------------------------------------------------

Die dritte Datei ist die KI. Diese muss je nach Spiel und Sprache unterschiedlich aussehen. Wie genau sieht man in den jeweiligen SimplePlayers.


Frontend API
============

Wir stellen euch sämtliche Funktionen dieser Webseite auch als API zur Verfügung.

Status der Dokumentation: <img src="http://online.swagger.io/validator?url=http://thuermchen.com/api.yaml">

Die Dokumentation ist [hier](http://thuermchen.com/api) online erreichbar.
