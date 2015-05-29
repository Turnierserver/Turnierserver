###Frontend

Server, der den Usern eine Webseite (mit ESU-API) liefert, auf der sie ihre KI managen und spielen lassen können.
Sprache: Python 3 (mit [Flask](http://flask.pocoo.org))

Libs:
- [Flask](http://flask.pocoo.org)
- [SQLAlchemy](http://www.sqlalchemy.org)
- [mehr in der requirements.txt](https://github.com/LuckyLukert/Turnierserver/blob/master/Frontend/requirements.txt)


Die Libs können mit

    pip3 install -r requirements.txt

installiert werden.

Der Server wird dann mit

    python3 app.py run

gestartet.

Es werden möglicherweise Teile vom alten Frontend übernommen und verbessert.


##API-Spezifikation (wird sich noch ändern)
[Hier] (https://github.com/LuckyLukert/Turnierserver/blob/master/Frontend/api.py) gibt es die aktuelle API implementation.

Alle implementierten sind gehäkelt.

#####GETs (offen zugänglich / ohne Authentifizierung)
-------------
- [x] /api/ais
  * *gibt eine Liste von KIs zurück, die deren Punktzahl und deren Besitzer beinhaltet.*
- [x] /api/ai/\<id\>
  * *gibt den Besitzer, die Punktzahl und ähnliches zurück.*
- [x] /api/ai/\<id\>/games
  * *gibt eine Liste von Spielen der KI zurück.*
- [x] /api/ai/\<id\>/icon
  * *gibt das Icon der KI zurück.*
- [x] /api/ai/\<id\>/compile
  * *startet den Kompilierungs-Vorgang.*
- [x] /api/users
  * *gibt eine Liste aller Nutzer zurück.*
- [x] /api/user/\<id\>
  * *gibt alle KIs und andere Infos über den Nutzer zurück.*
- [x] /api/user/\<id\>/icon
  * *gibt das Icon des Users zurück.*
- [x] /api/games
  * *gibt alle Spielids (und mehr infos) zurück.*
- [x] /api/game/\<id\>
  * *gibt Infos zurück.*
- [ ] /api/game/\<id\>/log
  * *gibt den Spielverlauf zurück.*

#####POSTs (mit Authentifizierung)
--------------
- [x] /api/ai/create
  * *erstellt eine neue KI, gibt deren Infos zurück.*
  * *die KI-Infos werden später mit /api/ai/\<id\>/update geändert.*
- [x] /api/user/create
  * *erstellt einen neuen User, nimmt 'username', 'email', 'password', 'firstname', 'lastname' an.*
- [x] /api/ai/\<id\>/update
  * *nimmt 'name', 'description', 'lang' und 'extra[]' an*
- [x] /api/ai/\<id\>/upload
  * *nimmt 'path', 'filename' und 'data' an.*
- [x] /api/ai/\<id\>/deleteFile
  * *nimmt 'path' und 'filename' an.*
- [x] /api/ai/\<id\>/create_folder
  * *nimmt 'path' und 'filename' an.*
- [x] /api/ai/\<id\>/delete
  * *löscht die KI*
- [x] /api/games/start
  * *nimmt 'ai[]' an und startet ein Spiel*

- [x] /api/ai/\<id\>/update
  * *updatet infos zur Ki.*
- [x] /api/user/update
  * *updatet infos zum Nutzer.*


#####Authentifizierung
--------------
Kekse:
  * [x] */api/login setzt Cookies und alle anderen Anfragen benutzen diese.*
  * [x] */api/loggedin gibt den eingeloggten Nutzer zurück (wenn es einen gibt).*
  * [x] */api/logout loggt aus.*
  * *sollte einfach integrierbar sein.*
