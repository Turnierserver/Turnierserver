Anmeldung und so:



## POST: /api/login
----------

#### Request:

Headers:

 - Content-Length: 35
 - Connection: keep-alive
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0
 - Content-Type: application/x-www-form-urlencoded
 - Accept-Encoding: gzip, deflate
 - Accept: */*


Body:
```
	password=admin&email=admin%40ad.min
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 16
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8oEQ.dn1qme5XgNKSHcZV6oSvM6NeHZg; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	{"error": false}
```


## POST: /api/loggedin
----------

#### Request:

Headers:

 - Content-Length: 0
 - Connection: keep-alive
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8oEQ.dn1qme5XgNKSHcZV6oSvM6NeHZg
 - Accept-Encoding: gzip, deflate
 - Accept: */*


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 52
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8oEQ.dn1qme5XgNKSHcZV6oSvM6NeHZg; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	{"ais": [], "admin": true, "id": 6, "name": "admin"}
```


## POST: /api/logout
----------

#### Request:

Headers:

 - Content-Length: 0
 - Connection: keep-alive
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8oEQ.dn1qme5XgNKSHcZV6oSvM6NeHZg
 - Accept-Encoding: gzip, deflate
 - Accept: */*


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 16
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	{"error": false}
```


## POST: /api/loggedin
----------

#### Request:

Headers:

 - Content-Length: 0
 - Connection: keep-alive
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA
 - Accept-Encoding: gzip, deflate
 - Accept: */*


Body:
```
	None
```

#### Response:

Statuscode:
	401
Headers:

 - content-length: 38
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	{"error": "Insufficient permissions."}
```Anonyme API Funktionen (brauchen keine Authentifizierung):



## GET: /api/ais
----------

#### Request:

Headers:

 - Accept-Encoding: gzip, deflate
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA
 - Connection: keep-alive
 - Accept: */*
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 1770
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	[{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "ahermiston", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 1, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "magnam"}, {"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "erasmo59", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 5, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "repudiandae"}, {"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "gberge", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 3, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "quisquam"}, {"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "winford21", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 4, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "minus"}, {"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "bschuster", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 2, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "perspiciatis"}]
```


## GET: /api/users
----------

#### Request:

Headers:

 - Accept-Encoding: gzip, deflate
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA
 - Connection: keep-alive
 - Accept: */*
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 2106
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	[{"ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "ahermiston", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 1, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "magnam"}], "admin": false, "id": 1, "name": "ahermiston"}, {"ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "bschuster", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 2, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "perspiciatis"}], "admin": false, "id": 2, "name": "bschuster"}, {"ais": [{"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "gberge", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 3, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "quisquam"}], "admin": false, "id": 3, "name": "gberge"}, {"ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "winford21", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 4, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "minus"}], "admin": false, "id": 4, "name": "winford21"}, {"ais": [{"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "erasmo59", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 5, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "repudiandae"}], "admin": false, "id": 5, "name": "erasmo59"}, {"ais": [], "admin": true, "id": 6, "name": "admin"}]
```


## GET: /api/games
----------

#### Request:

Headers:

 - Accept-Encoding: gzip, deflate
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA
 - Connection: keep-alive
 - Accept: */*
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 3850
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	[{"id": 1, "type": {"id": 1, "name": "Minesweeper"}, "ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "ahermiston", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 1, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "magnam"}, {"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "bschuster", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 2, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "perspiciatis"}]}, {"id": 2, "type": {"id": 1, "name": "Minesweeper"}, "ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "ahermiston", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 1, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "magnam"}, {"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "erasmo59", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 5, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "repudiandae"}]}, {"id": 3, "type": {"id": 1, "name": "Minesweeper"}, "ais": [{"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "erasmo59", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 5, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "repudiandae"}, {"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "gberge", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 3, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "quisquam"}]}, {"id": 4, "type": {"id": 1, "name": "Minesweeper"}, "ais": [{"lang": {"url": "https://esolangs.org/wiki/Brainfuck", "id": 3, "name": "Brainfuck"}, "author": "gberge", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 3, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "quisquam"}, {"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "winford21", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 4, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "minus"}]}, {"id": 5, "type": {"id": 1, "name": "Minesweeper"}, "ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "winford21", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 4, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "minus"}, {"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "bschuster", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 2, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "perspiciatis"}]}]
```Anonyme API Funktionen von bestimmten Objekten:



## GET: /api/ai/1
----------

#### Request:

Headers:

 - Accept-Encoding: gzip, deflate
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA
 - Connection: keep-alive
 - Accept: */*
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 345
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "ahermiston", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 1, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "magnam"}
```


## GET: /api/user/1
----------

#### Request:

Headers:

 - Accept-Encoding: gzip, deflate
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA
 - Connection: keep-alive
 - Accept: */*
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 403
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	{"ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "ahermiston", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 1, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "magnam"}], "admin": false, "id": 1, "name": "ahermiston"}
```


## GET: /api/game/1
----------

#### Request:

Headers:

 - Accept-Encoding: gzip, deflate
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA
 - Connection: keep-alive
 - Accept: */*
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 763
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8oEQ.vTbYYjoN4NLGoLgvXPeykrsO8kA; HttpOnly; Path=/
 - date: Tue, 02 Jun 2015 11:42:09 GMT
 - content-type: application/json


Body:
```
	{"id": 1, "type": {"id": 1, "name": "Minesweeper"}, "ais": [{"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "ahermiston", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 1, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "magnam"}, {"lang": {"url": "https://www.java.com/?isthaesslig=1", "id": 2, "name": "Java"}, "author": "bschuster", "gametype": {"id": 1, "name": "Minesweeper"}, "author_id": 2, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "versions": [{"compiled": false, "frozen": false, "qualified": false, "id": 1, "extras": []}], "name": "perspiciatis"}]}
```