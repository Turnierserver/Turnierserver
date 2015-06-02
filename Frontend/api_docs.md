PingPongPingPong



## GET: /api
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 5
 - content-type: text/html; charset=utf-8
 - server: Werkzeug/0.10.4 Python/3.4.3


Body:
```
PONG!
```
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
	password=admin&email=admin%40ad.min
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 16
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE-bIA.13ODgG2L52dJWEXO1nlZejuyGsU; HttpOnly; Path=/


Body:
```
{'error': False}
```



## POST: /api/loggedin
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 0
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE-bIA.13ODgG2L52dJWEXO1nlZejuyGsU
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
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE-bIA.13ODgG2L52dJWEXO1nlZejuyGsU; HttpOnly; Path=/


Body:
```
{'admin': True, 'ais': [], 'id': 6, 'name': 'admin'}
```



## POST: /api/logout
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 0
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE-bIA.13ODgG2L52dJWEXO1nlZejuyGsU
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
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
{'error': False}
```



## POST: /api/loggedin
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 0
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
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
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
{'error': 'Insufficient permissions.'}
```
Anonyme API Funktionen (brauchen keine Authentifizierung):



## GET: /api/ais
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 1824
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
[{'author': 'rose.gutmann',
  'author_id': 5,
  'description': 'Et eos illo fugiat sit quis rerum voluptatem.',
  'gametype': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'},
  'id': 1,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'et',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'skiles.jesenia',
  'author_id': 2,
  'description': 'Nesciunt modi qui quo animi facere accusantium.',
  'gametype': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'},
  'id': 2,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'aut',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'aarav.bayer',
  'author_id': 3,
  'description': 'Top Keks',
  'gametype': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'},
  'id': 3,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'Top Keks',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'owaelchi',
  'author_id': 1,
  'description': 'Ratione aut rem mollitia in.',
  'gametype': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'},
  'id': 4,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'aliquid',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'rita.stanton',
  'author_id': 4,
  'description': 'Est possimus quos repellendus quo.',
  'gametype': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'},
  'id': 5,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'vel',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]}]
```



## GET: /api/users
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 2175
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
[{'admin': False,
  'ais': [{'author': 'owaelchi',
           'author_id': 1,
           'description': 'Ratione aut rem mollitia in.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'aliquid',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 1,
  'name': 'owaelchi'},
 {'admin': False,
  'ais': [{'author': 'skiles.jesenia',
           'author_id': 2,
           'description': 'Nesciunt modi qui quo animi facere accusantium.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'aut',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 2,
  'name': 'skiles.jesenia'},
 {'admin': False,
  'ais': [{'author': 'aarav.bayer',
           'author_id': 3,
           'description': 'Top Keks',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'Top Keks',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 3,
  'name': 'aarav.bayer'},
 {'admin': False,
  'ais': [{'author': 'rita.stanton',
           'author_id': 4,
           'description': 'Est possimus quos repellendus quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'vel',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 4,
  'name': 'rita.stanton'},
 {'admin': False,
  'ais': [{'author': 'rose.gutmann',
           'author_id': 5,
           'description': 'Et eos illo fugiat sit quis rerum voluptatem.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'et',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 5,
  'name': 'rose.gutmann'},
 {'admin': True, 'ais': [], 'id': 6, 'name': 'admin'}]
```



## GET: /api/games
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 4103
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
[{'ais': [{'author': 'rose.gutmann',
           'author_id': 5,
           'description': 'Et eos illo fugiat sit quis rerum voluptatem.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'et',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'rita.stanton',
           'author_id': 4,
           'description': 'Est possimus quos repellendus quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'vel',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 1,
  'type': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'rose.gutmann',
           'author_id': 5,
           'description': 'Et eos illo fugiat sit quis rerum voluptatem.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'et',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'skiles.jesenia',
           'author_id': 2,
           'description': 'Nesciunt modi qui quo animi facere accusantium.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'aut',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 2,
  'type': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'skiles.jesenia',
           'author_id': 2,
           'description': 'Nesciunt modi qui quo animi facere accusantium.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'aut',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'aarav.bayer',
           'author_id': 3,
           'description': 'Top Keks',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'Top Keks',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 3,
  'type': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'aarav.bayer',
           'author_id': 3,
           'description': 'Top Keks',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'Top Keks',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'owaelchi',
           'author_id': 1,
           'description': 'Ratione aut rem mollitia in.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'aliquid',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 4,
  'type': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'owaelchi',
           'author_id': 1,
           'description': 'Ratione aut rem mollitia in.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'aliquid',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'rita.stanton',
           'author_id': 4,
           'description': 'Est possimus quos repellendus quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433274766,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'vel',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 5,
  'type': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'}}]
```



## GET: /api/langs
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 208
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
[{'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
 {'id': 2, 'name': 'Java', 'url': 'https://www.java.com/?isthaesslig=1'},
 {'id': 3, 'name': 'C++', 'url': 'http://en.wikipedia.org/wiki/C%2B%2B'}]
```



## GET: /api/gametypes
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 63
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
[{'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'}]
```
Anonyme API Funktionen von bestimmten Objekten:



## GET: /api/ai/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 380
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
{'author': 'rose.gutmann',
 'author_id': 5,
 'description': 'Et eos illo fugiat sit quis rerum voluptatem.',
 'gametype': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'},
 'id': 1,
 'lang': {'id': 2,
          'name': 'Java',
          'url': 'https://www.java.com/?isthaesslig=1'},
 'name': 'et',
 'versions': [{'compiled': False,
               'extras': [],
               'frozen': False,
               'id': 1,
               'qualified': False}]}
```



## GET: /api/user/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 409
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
{'admin': False,
 'ais': [{'author': 'owaelchi',
          'author_id': 1,
          'description': 'Ratione aut rem mollitia in.',
          'gametype': {'id': 1,
                       'last_modified': 1433274766,
                       'name': 'Minesweeper'},
          'id': 4,
          'lang': {'id': 1,
                   'name': 'Python',
                   'url': 'https://www.python.org'},
          'name': 'aliquid',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]}],
 'id': 1,
 'name': 'owaelchi'}
```



## GET: /api/game/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 832
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc; HttpOnly; Path=/


Body:
```
{'ais': [{'author': 'rose.gutmann',
          'author_id': 5,
          'description': 'Et eos illo fugiat sit quis rerum voluptatem.',
          'gametype': {'id': 1,
                       'last_modified': 1433274766,
                       'name': 'Minesweeper'},
          'id': 1,
          'lang': {'id': 2,
                   'name': 'Java',
                   'url': 'https://www.java.com/?isthaesslig=1'},
          'name': 'et',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]},
         {'author': 'rita.stanton',
          'author_id': 4,
          'description': 'Est possimus quos repellendus quo.',
          'gametype': {'id': 1,
                       'last_modified': 1433274766,
                       'name': 'Minesweeper'},
          'id': 5,
          'lang': {'id': 1,
                   'name': 'Python',
                   'url': 'https://www.python.org'},
          'name': 'vel',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]}],
 'id': 1,
 'type': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'}}
```
Funkionen mit Authentifizierung



## POST: /api/login
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 35
 - Content-Type: application/x-www-form-urlencoded
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE-bIA.0dU-SMNEdIZV0kmy_zItYIqxKfc
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	password=admin&email=admin%40ad.min
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 16
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJyVjU0KwjAQRq8SZh2kRBPTrL2FFMnPmASKlczETendDd7A1eODx_d2eDxXTwUJ3H0HwQPw3qhy_SBIuHVRPLFINRaB9ZVx3XLmEyyH_E9f5Eg1pAKOW8exagIHZ6-ni_bWqDQr1PGqgjVJa1Qh2DhP47QTtp9sji_xXTg1.CE-bIQ.BTftdK1PCAeTimscpgDFrfnl4iI; HttpOnly; Path=/


Body:
```
{'error': False}
```



## POST: /api/ai/3/update
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 41
 - Content-Type: application/x-www-form-urlencoded
 - Cookie: session=.eJyVjU0KwjAQRq8SZh2kRBPTrL2FFMnPmASKlczETendDd7A1eODx_d2eDxXTwUJ3H0HwQPw3qhy_SBIuHVRPLFINRaB9ZVx3XLmEyyH_E9f5Eg1pAKOW8exagIHZ6-ni_bWqDQr1PGqgjVJa1Qh2DhP47QTtp9sji_xXTg1.CE-bIQ.BTftdK1PCAeTimscpgDFrfnl4iI
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	description=Top+Keks&name=Top+Keks&lang=1
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 337
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJyVjU0KwjAQRq8SZh2kRBPTrL2FFMnPmASKlczETendDd7A1eODx_d2eDxXTwUJ3H0HwQPw3qhy_SBIuHVRPLFINRaB9ZVx3XLmEyyH_E9f5Eg1pAKOW8exagIHZ6-ni_bWqDQr1PGqgjVJa1Qh2DhP47QTtp9sji_xXTg1.CE-bIg.kkltHulI7Ofm9M0jfn_wisM7Kjg; HttpOnly; Path=/


Body:
```
{'author': 'aarav.bayer',
 'author_id': 3,
 'description': 'Top Keks',
 'gametype': {'id': 1, 'last_modified': 1433274766, 'name': 'Minesweeper'},
 'id': 3,
 'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
 'name': 'Top Keks',
 'versions': [{'compiled': False,
               'extras': [],
               'frozen': False,
               'id': 1,
               'qualified': False}]}
```
