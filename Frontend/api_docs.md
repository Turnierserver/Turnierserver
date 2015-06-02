Anmeldung und so:



## POST: /api/login
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 35
 - Content-Type: application/x-www-form-urlencoded
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	email=admin%40ad.min&password=admin
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 16
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8qSg.evpKLU0Grh8xFRgZWVgle6moCsE; HttpOnly; Path=/


Body:
```
	{"error": false}
```



## POST: /api/loggedin
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 0
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8qSg.evpKLU0Grh8xFRgZWVgle6moCsE
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 52
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8qSg.evpKLU0Grh8xFRgZWVgle6moCsE; HttpOnly; Path=/


Body:
```
	{"name": "admin", "id": 6, "admin": true, "ais": []}
```



## POST: /api/logout
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 0
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8qSg.evpKLU0Grh8xFRgZWVgle6moCsE
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 16
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	{"error": false}
```



## POST: /api/loggedin
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 0
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	401
Headers:

 - content-length: 38
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	{"error": "Insufficient permissions."}
```
Anonyme API Funktionen (brauchen keine Authentifizierung):



## GET: /api/ais
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw
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
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	[{"gametype": {"name": "Minesweeper", "id": 1}, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "name": "magnam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "ahermiston", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 1}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "name": "repudiandae", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "erasmo59", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 5}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "name": "quisquam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "gberge", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 3}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "name": "minus", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "winford21", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 4}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "name": "perspiciatis", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "bschuster", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 2}]
```



## GET: /api/users
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw
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
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	[{"name": "ahermiston", "id": 1, "admin": false, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "name": "magnam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "ahermiston", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 1}]}, {"name": "bschuster", "id": 2, "admin": false, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "name": "perspiciatis", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "bschuster", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 2}]}, {"name": "gberge", "id": 3, "admin": false, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "name": "quisquam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "gberge", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 3}]}, {"name": "winford21", "id": 4, "admin": false, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "name": "minus", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "winford21", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 4}]}, {"name": "erasmo59", "id": 5, "admin": false, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "name": "repudiandae", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "erasmo59", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 5}]}, {"name": "admin", "id": 6, "admin": true, "ais": []}]
```



## GET: /api/games
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw
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
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	[{"id": 1, "type": {"name": "Minesweeper", "id": 1}, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "name": "magnam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "ahermiston", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 1}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "name": "perspiciatis", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "bschuster", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 2}]}, {"id": 2, "type": {"name": "Minesweeper", "id": 1}, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "name": "magnam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "ahermiston", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 1}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "name": "repudiandae", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "erasmo59", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 5}]}, {"id": 3, "type": {"name": "Minesweeper", "id": 1}, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 2, "description": "Quas tempore itaque commodi dolorem voluptatem.", "name": "repudiandae", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "erasmo59", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 5}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "name": "quisquam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "gberge", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 3}]}, {"id": 4, "type": {"name": "Minesweeper", "id": 1}, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 3, "description": "Quae est itaque aliquid aut dicta qui et.", "name": "quisquam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "gberge", "lang": {"name": "Brainfuck", "id": 3, "url": "https://esolangs.org/wiki/Brainfuck"}, "author_id": 3}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "name": "minus", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "winford21", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 4}]}, {"id": 5, "type": {"name": "Minesweeper", "id": 1}, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 4, "description": "Velit magni quidem non totam quia ipsum.", "name": "minus", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "winford21", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 4}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "name": "perspiciatis", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "bschuster", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 2}]}]
```
Anonyme API Funktionen von bestimmten Objekten:



## GET: /api/ai/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw
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
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	{"gametype": {"name": "Minesweeper", "id": 1}, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "name": "magnam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "ahermiston", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 1}
```



## GET: /api/user/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw
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
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	{"name": "ahermiston", "id": 1, "admin": false, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "name": "magnam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "ahermiston", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 1}]}
```



## GET: /api/game/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw
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
 - content-type: application/json
 - date: Tue, 02 Jun 2015 11:51:38 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8qSg.ym094Ve5iqWV-WwhLmrR_A0p8Tw; HttpOnly; Path=/


Body:
```
	{"id": 1, "type": {"name": "Minesweeper", "id": 1}, "ais": [{"gametype": {"name": "Minesweeper", "id": 1}, "id": 1, "description": "Ut et asperiores delectus nihil iste.", "name": "magnam", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "ahermiston", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 1}, {"gametype": {"name": "Minesweeper", "id": 1}, "id": 5, "description": "Et dolor recusandae delectus ut sapiente.", "name": "perspiciatis", "versions": [{"compiled": false, "id": 1, "extras": [], "qualified": false, "frozen": false}], "author": "bschuster", "lang": {"name": "Java", "id": 2, "url": "https://www.java.com/?isthaesslig=1"}, "author_id": 2}]}
```
