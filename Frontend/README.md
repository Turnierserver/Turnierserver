###Frontend

Server, der den Usern eine Webseite (mit ESU-API) liefert, auf der sie ihre KI managen und spielen lassen können.
Sprache: Python (mit [Django](https://www.djangoproject.com))

Es werden höchstwahrscheinlicht große Teile vom alten Frontend übernommen und verbessert.


##API-Spezifikation (wird sich noch ändern)

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

#####POSTs (verändern was - brauchen Authentifizierung)
--------------
- /api/ai/create
- /api/ai/\<id\>/submitCode
  * *multipart uploading?*
- /api/ai/\<id\>/fight?id=\<id\>&id=\<id\>
  * *alternativ Layouts:*
  * */api/ais/fight/\<id\>/\<id\>/...*
  * */api/ai/\<id\>/fight/\<id\>/*
  * */api/ai/\<id\>/fight?ids=[\<id\>, \<id\>]*


#####PUTs (verändern was - brauchen Authentifizierung)
--------------
- /api/ai/\<id\>/update
  * *updatet infos zur Ki.*
- /api/user/update
  * *updatet infos zum Nutzer.*


#####Authentifizierungsmöglichkeiten
--------------
- jeder Nutzer hat einen API-key
  * *einfach und keine großen Sicherheitsprobleme.*
- OAuth (1 oder 2)
  * *braucht Bibliotheken und ist komisch, wird aber überall benutzt.*
