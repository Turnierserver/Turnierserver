###Frontend

Server, der den Usern eine Webseite (mit ESU-API) liefert, auf der sie ihre KI managen und spielen lassen können.
Sprache: Python (mit [Django](https://www.djangoproject.com))

Es werden höchstwahrscheinlicht große Teile vom alten Frontend übernommen und verbessert.


##API-Spezifikation (wird sich noch ändern)

[Hier] (https://github.com/LuckyLukert/Turnierserver/blob/master/Frontend/testapi.py) gibt es eine API implementation, gegen die man sein Zeugs testen kann.

#####GETs (offen zugänglich / ohne Authentifizierung)
-------------
- /api/ais
  * *gibt eine Liste von KIs zurück, die deren Punktzahl und deren Besitzer beinhaltet.*
- /api/ai/\<id\>
  * *gibt den Besitzer, die Punktzahl und ähnliches zurück.*
- /api/users
  * *gibt eine Liste aller Nutzer zurück.*
- /api/user/\<id\>
  * *gibt alle KIs und andere Infos über den Nutzer zurück.*
- /api/games
  * *gibt alle Spielids (und mehr infos?) zurück.*
- /api/game/\<id\>
  * *gibt Infos (und Spielverlauf?) zurück.*

#####POSTs (mit Authentifizierung)
--------------
- /api/ai/create
- /api/ai/\<id\>/submitCode
  * *multipart uploading?*
- /api/ai/\<id\>/fight?id=\<id\>&id=\<id\>
  * *alternativ Layouts:*
  * */api/ais/fight/\<id\>/\<id\>/...*
  * */api/ai/\<id\>/fight/\<id\>/*
  * */api/ai/\<id\>/fight?ids=[\<id\>, \<id\>]*

- /api/ai/\<id\>/update
  * *updatet infos zur Ki.*
- /api/user/update
  * *updatet infos zum Nutzer.*


#####Authentifizierung
--------------
Kekse:
  * */api/login setzt Cookies und alle anderen Anfragen benutzen diese.*
  * */api/logout loggt aus.*
  * *sollte einfach integrierbar sein.*
