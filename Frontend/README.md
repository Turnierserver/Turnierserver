###Frontend

Server, der den Usern eine Webseite (mit ESU-API) liefert, auf der sie ihre KI managen und spielen lassen können.
Sprache: Python (mit [Django](https://www.djangoproject.com))

Es werden höchstwahrscheinlicht große Teile vom alten Frontend übernommen und verbessert.


##API-Spezifikation (wird sich noch ändern)

#####GETs
-------------
- /api/ais
  * *gibt eine Liste von KIs zurück, die deren Punktzahl und deren Besitzer beinhaltet.*
- /api/ai/<id>
  * *gibt den Besitzer, die Punktzahl und ähnliches zurück.*
- /api/users
  * *gibt eine Liste aller Nutzer zurück.*
- /api/user/<id>
  * *gibt alle KIs und andere Infos über den Nutzer zurück.*
- /api/games
  * *gibt alle Spielids (und mehr infos?) zurück.*
- /api/game/<id>
  * *gibt Infos (und Spielverlauf?) zurück.*

#####POSTs
--------------

#####PUTs
--------------
