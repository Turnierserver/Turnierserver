###Frontend

Server, der den Usern eine Webseite (mit ESU-API) liefert, auf der sie ihre KI managen und spielen lassen können.
Sprache: Python 3 (mit [Flask](http://flask.pocoo.org))

Libs:
- [Flask](http://flask.pocoo.org)
- [SQLAlchemy](http://www.sqlalchemy.org)
- [mehr in der requirements.txt](https://github.com/LuckyLukert/Turnierserver/blob/master/Frontend/requirements.txt)


Die Libs können mit

    pip3 install -r requirements.txt

installiert werden

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
- [x] /api/users
  * *gibt eine Liste aller Nutzer zurück.*
- [x] /api/user/\<id\>
  * *gibt alle KIs und andere Infos über den Nutzer zurück.*
- [x] /api/games
  * *gibt alle Spielids (und mehr infos) zurück.*
- [x] /api/game/\<id\>
  * *gibt Infos zurück.*
- [ ] /api/game/\<id\>/log
  * *gibt den Spielverlauf zurück.*

#####POSTs (mit Authentifizierung)
--------------
- [x] /api/ai/create
- [ ] /api/ai/\<id\>/submitCode
  * *multipart uploading?*
- [ ] /api/ai/\<id\>/fight?id=\<id\>&id=\<id\>
  * *alternativ Layouts:*
  * */api/ais/fight/\<id\>/\<id\>/...*
  * */api/ai/\<id\>/fight/\<id\>/*
  * */api/ai/\<id\>/fight?ids=[\<id\>, \<id\>]*

- [x] /api/ai/\<id\>/update
  * *updatet infos zur Ki.*
- [x] /api/user/update
  * *updatet infos zum Nutzer.*


#####Authentifizierung
--------------
Kekse:
  * [x] */api/login setzt Cookies und alle anderen Anfragen benutzen diese.*
  * [x] */api/logout loggt aus.*
  * *sollte einfach integrierbar sein.*
