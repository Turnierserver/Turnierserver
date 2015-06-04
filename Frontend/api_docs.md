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
	email=admin%40ad.min&password=admin
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 16
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CFHMZA.f65CAUCXSetBTh1cbntVbW_x_vg; HttpOnly; Path=/


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
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CFHMZA.f65CAUCXSetBTh1cbntVbW_x_vg
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 382
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CFHMZA.f65CAUCXSetBTh1cbntVbW_x_vg; HttpOnly; Path=/


Body:
```
{'ais': [{'author': 'admin',
          'author_id': 6,
          'description': 'Unbeschriebene KI',
          'gametype': {'id': 1,
                       'last_modified': 1433413387,
                       'name': 'Minesweeper'},
          'id': 6,
          'lang': {'id': 1,
                   'name': 'Python',
                   'url': 'https://www.python.org'},
          'name': 'Unbenannte KI',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]}],
 'id': 6,
 'name': 'admin'}
```



## POST: /api/logout
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Content-Length: 0
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CFHMZA.f65CAUCXSetBTh1cbntVbW_x_vg
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
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
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
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 2234
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
[{'author': 'sschuppe',
  'author_id': 3,
  'description': 'Commodi alias iste aut velit eius id.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 1,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'nulla',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'cap44',
  'author_id': 4,
  'description': 'Commodi est ea molestiae maxime odio quisquam.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 2,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'asperiores',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'schumm.jeffie',
  'author_id': 5,
  'description': 'Autem qui aut eum molestiae provident et debitis.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 3,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'omnis',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'quitzon.margarite',
  'author_id': 1,
  'description': 'Omnis amet quo alias voluptatem omnis quo.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 4,
  'lang': {'id': 3,
           'name': 'C++',
           'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
  'name': 'dolore',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'kenley56',
  'author_id': 2,
  'description': 'Velit facilis labore rerum delectus.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 5,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'dolores',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'admin',
  'author_id': 6,
  'description': 'Unbeschriebene KI',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 6,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'Unbenannte KI',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]}]
```



## GET: /api/ais/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 2234
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
[{'author': 'sschuppe',
  'author_id': 3,
  'description': 'Commodi alias iste aut velit eius id.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 1,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'nulla',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'cap44',
  'author_id': 4,
  'description': 'Commodi est ea molestiae maxime odio quisquam.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 2,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'asperiores',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'schumm.jeffie',
  'author_id': 5,
  'description': 'Autem qui aut eum molestiae provident et debitis.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 3,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'omnis',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'quitzon.margarite',
  'author_id': 1,
  'description': 'Omnis amet quo alias voluptatem omnis quo.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 4,
  'lang': {'id': 3,
           'name': 'C++',
           'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
  'name': 'dolore',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'kenley56',
  'author_id': 2,
  'description': 'Velit facilis labore rerum delectus.',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 5,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'dolores',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'admin',
  'author_id': 6,
  'description': 'Unbeschriebene KI',
  'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
  'id': 6,
  'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
  'name': 'Unbenannte KI',
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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 2641
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
[{'ais': [{'author': 'quitzon.margarite',
           'author_id': 1,
           'description': 'Omnis amet quo alias voluptatem omnis quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 3,
                    'name': 'C++',
                    'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
           'name': 'dolore',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 1,
  'name': 'quitzon.margarite'},
 {'ais': [{'author': 'kenley56',
           'author_id': 2,
           'description': 'Velit facilis labore rerum delectus.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'dolores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 2,
  'name': 'kenley56'},
 {'ais': [{'author': 'sschuppe',
           'author_id': 3,
           'description': 'Commodi alias iste aut velit eius id.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'nulla',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 3,
  'name': 'sschuppe'},
 {'ais': [{'author': 'cap44',
           'author_id': 4,
           'description': 'Commodi est ea molestiae maxime odio quisquam.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'asperiores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 4,
  'name': 'cap44'},
 {'ais': [{'author': 'schumm.jeffie',
           'author_id': 5,
           'description': 'Autem qui aut eum molestiae provident et '
                          'debitis.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'omnis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 5,
  'name': 'schumm.jeffie'},
 {'ais': [{'author': 'admin',
           'author_id': 6,
           'description': 'Unbeschriebene KI',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 6,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'Unbenannte KI',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 6,
  'name': 'admin'},
 {'ais': [], 'id': 7, 'name': 'Mrmaxmeier'},
 {'ais': [], 'id': 8, 'name': 'msrd0'},
 {'ais': [], 'id': 9, 'name': 'fhjnuishnfjushuisfdhjuisfhjuisfdahusasfdhu'}]
```



## GET: /api/games
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 4229
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
[{'ais': [{'author': 'sschuppe',
           'author_id': 3,
           'description': 'Commodi alias iste aut velit eius id.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'nulla',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'kenley56',
           'author_id': 2,
           'description': 'Velit facilis labore rerum delectus.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'dolores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 1,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'sschuppe',
           'author_id': 3,
           'description': 'Commodi alias iste aut velit eius id.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'nulla',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'cap44',
           'author_id': 4,
           'description': 'Commodi est ea molestiae maxime odio quisquam.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'asperiores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 2,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'cap44',
           'author_id': 4,
           'description': 'Commodi est ea molestiae maxime odio quisquam.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'asperiores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'schumm.jeffie',
           'author_id': 5,
           'description': 'Autem qui aut eum molestiae provident et '
                          'debitis.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'omnis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 3,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'schumm.jeffie',
           'author_id': 5,
           'description': 'Autem qui aut eum molestiae provident et '
                          'debitis.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'omnis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'quitzon.margarite',
           'author_id': 1,
           'description': 'Omnis amet quo alias voluptatem omnis quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 3,
                    'name': 'C++',
                    'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
           'name': 'dolore',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 4,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'quitzon.margarite',
           'author_id': 1,
           'description': 'Omnis amet quo alias voluptatem omnis quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 3,
                    'name': 'C++',
                    'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
           'name': 'dolore',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'kenley56',
           'author_id': 2,
           'description': 'Velit facilis labore rerum delectus.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'dolores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 5,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}}]
```



## GET: /api/games/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 4229
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
[{'ais': [{'author': 'sschuppe',
           'author_id': 3,
           'description': 'Commodi alias iste aut velit eius id.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'nulla',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'kenley56',
           'author_id': 2,
           'description': 'Velit facilis labore rerum delectus.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'dolores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 1,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'sschuppe',
           'author_id': 3,
           'description': 'Commodi alias iste aut velit eius id.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'nulla',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'cap44',
           'author_id': 4,
           'description': 'Commodi est ea molestiae maxime odio quisquam.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'asperiores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 2,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'cap44',
           'author_id': 4,
           'description': 'Commodi est ea molestiae maxime odio quisquam.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'asperiores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'schumm.jeffie',
           'author_id': 5,
           'description': 'Autem qui aut eum molestiae provident et '
                          'debitis.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'omnis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 3,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'schumm.jeffie',
           'author_id': 5,
           'description': 'Autem qui aut eum molestiae provident et '
                          'debitis.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 1,
                    'name': 'Python',
                    'url': 'https://www.python.org'},
           'name': 'omnis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'quitzon.margarite',
           'author_id': 1,
           'description': 'Omnis amet quo alias voluptatem omnis quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 3,
                    'name': 'C++',
                    'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
           'name': 'dolore',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 4,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'quitzon.margarite',
           'author_id': 1,
           'description': 'Omnis amet quo alias voluptatem omnis quo.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 3,
                    'name': 'C++',
                    'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
           'name': 'dolore',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'kenley56',
           'author_id': 2,
           'description': 'Velit facilis labore rerum delectus.',
           'gametype': {'id': 1,
                        'last_modified': 1433413387,
                        'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'dolores',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 5,
  'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}}]
```



## GET: /api/langs
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
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
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
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
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
[{'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}]
```
Anonyme API Funktionen von bestimmten Objekten:



## GET: /api/ai/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 371
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
{'author': 'sschuppe',
 'author_id': 3,
 'description': 'Commodi alias iste aut velit eius id.',
 'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
 'id': 1,
 'lang': {'id': 2,
          'name': 'Java',
          'url': 'https://www.java.com/?isthaesslig=1'},
 'name': 'nulla',
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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 435
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
{'ais': [{'author': 'quitzon.margarite',
          'author_id': 1,
          'description': 'Omnis amet quo alias voluptatem omnis quo.',
          'gametype': {'id': 1,
                       'last_modified': 1433413387,
                       'name': 'Minesweeper'},
          'id': 4,
          'lang': {'id': 3,
                   'name': 'C++',
                   'url': 'http://en.wikipedia.org/wiki/C%2B%2B'},
          'name': 'dolore',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]}],
 'id': 1,
 'name': 'quitzon.margarite'}
```



## GET: /api/game/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	None
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 836
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU; HttpOnly; Path=/


Body:
```
{'ais': [{'author': 'sschuppe',
          'author_id': 3,
          'description': 'Commodi alias iste aut velit eius id.',
          'gametype': {'id': 1,
                       'last_modified': 1433413387,
                       'name': 'Minesweeper'},
          'id': 1,
          'lang': {'id': 2,
                   'name': 'Java',
                   'url': 'https://www.java.com/?isthaesslig=1'},
          'name': 'nulla',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]},
         {'author': 'kenley56',
          'author_id': 2,
          'description': 'Velit facilis labore rerum delectus.',
          'gametype': {'id': 1,
                       'last_modified': 1433413387,
                       'name': 'Minesweeper'},
          'id': 5,
          'lang': {'id': 2,
                   'name': 'Java',
                   'url': 'https://www.java.com/?isthaesslig=1'},
          'name': 'dolores',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]}],
 'id': 1,
 'type': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'}}
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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CFHMZA.c3-H1DMtXIaa2YHjCUWLRsmjcxU
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
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJyVjU0KwjAQRq8SZh2kRBPTrL2FFMnPmASKlczETendDd7A1eODx_d2eDxXTwUJ3H0HwQPw3qhy_SBIuHVRPLFINRaB9ZVx3XLmEyyH_E9f5Eg1pAKOW8exagIHZ6-ni_bWqDQr1PGqgjVJa1Qh2DhP47QTtp9sji_xXTg1.CFHMZA.m0VmOkrSr0W-titNMQ5pwy7eh84; HttpOnly; Path=/


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
 - Cookie: session=.eJyVjU0KwjAQRq8SZh2kRBPTrL2FFMnPmASKlczETendDd7A1eODx_d2eDxXTwUJ3H0HwQPw3qhy_SBIuHVRPLFINRaB9ZVx3XLmEyyH_E9f5Eg1pAKOW8exagIHZ6-ni_bWqDQr1PGqgjVJa1Qh2DhP47QTtp9sji_xXTg1.CFHMZA.m0VmOkrSr0W-titNMQ5pwy7eh84
 - User-Agent: python-requests/2.7.0 CPython/3.4.3 Darwin/14.4.0


Body:
```
	name=Top+Keks&lang=1&description=Top+Keks
```

#### Response:

Statuscode:
	200
Headers:

 - content-length: 339
 - content-type: application/json
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJyVjU0KwjAQRq8SZh2kRBPTrL2FFMnPmASKlczETendDd7A1eODx_d2eDxXTwUJ3H0HwQPw3qhy_SBIuHVRPLFINRaB9ZVx3XLmEyyH_E9f5Eg1pAKOW8exagIHZ6-ni_bWqDQr1PGqgjVJa1Qh2DhP47QTtp9sji_xXTg1.CFHMZA.m0VmOkrSr0W-titNMQ5pwy7eh84; HttpOnly; Path=/


Body:
```
{'author': 'schumm.jeffie',
 'author_id': 5,
 'description': 'Top Keks',
 'gametype': {'id': 1, 'last_modified': 1433413387, 'name': 'Minesweeper'},
 'id': 3,
 'lang': {'id': 1, 'name': 'Python', 'url': 'https://www.python.org'},
 'name': 'Top Keks',
 'versions': [{'compiled': False,
               'extras': [],
               'frozen': False,
               'id': 1,
               'qualified': False}]}
```
