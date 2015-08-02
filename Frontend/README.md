###Frontend

Server, der den Usern eine Webseite (mit ESU-API) liefert, auf der sie ihre KI managen und spielen lassen können.
Sprache: Python 3

Libs:
- [Flask](http://flask.pocoo.org)
- [SQLAlchemy](http://www.sqlalchemy.org)
- [mehr in der requirements.txt](https://github.com/LuckyLukert/Turnierserver/blob/master/Frontend/requirements.txt)


Die Libs können mit

    pip3 install -r requirements.txt

installiert werden.
(Achtung psycopg2 braucht eine Postgresql installation)

Der Server wird dann mit

    python3 app.py run

gestartet.

##API-Spezifikation
Status: <img src="http://online.swagger.io/validator?url=http://thuermchen.com/api.yaml">

[Hier implementiert](https://github.com/LuckyLukert/Turnierserver/blob/master/Frontend/api.py) gibt es die aktuelle API implementation, die [hier dokumentiert](https://github.com/LuckyLukert/Turnierserver/blob/master/Frontend/api.yaml) ist.

Die Dokumentation ist [hier](http://thuermchen.com/api) online erreichbar.

