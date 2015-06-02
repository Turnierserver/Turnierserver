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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8rdw.HsKJAY1YhuNENEXzQTBTvYJft1o; HttpOnly; Path=/


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
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8rdw.HsKJAY1YhuNENEXzQTBTvYJft1o
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8rdw.HsKJAY1YhuNENEXzQTBTvYJft1o; HttpOnly; Path=/


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
 - Cookie: session=.eJwdjMsKwyAQRX9FZh1KsNUa1_2LEIKPqQ6EpjjaTci_R7q6HDjnHrC-N8cZGex8gKh94LszVfohDPBqIjuuIlLIAumTcNtTqjdYzmXobUHOYGtp2IkiWLg7NT6UM1rGSaIKT-mNjkqh9N6EaeynjbH8ZX1ekJQobA.CE8rdw.HsKJAY1YhuNENEXzQTBTvYJft1o
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


Body:
```
[{'author': 'ahermiston',
  'author_id': 1,
  'description': 'Ut et asperiores delectus nihil iste.',
  'gametype': {'id': 1, 'name': 'Minesweeper'},
  'id': 1,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'magnam',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'erasmo59',
  'author_id': 5,
  'description': 'Quas tempore itaque commodi dolorem voluptatem.',
  'gametype': {'id': 1, 'name': 'Minesweeper'},
  'id': 2,
  'lang': {'id': 3,
           'name': 'Brainfuck',
           'url': 'https://esolangs.org/wiki/Brainfuck'},
  'name': 'repudiandae',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'gberge',
  'author_id': 3,
  'description': 'Quae est itaque aliquid aut dicta qui et.',
  'gametype': {'id': 1, 'name': 'Minesweeper'},
  'id': 3,
  'lang': {'id': 3,
           'name': 'Brainfuck',
           'url': 'https://esolangs.org/wiki/Brainfuck'},
  'name': 'quisquam',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'winford21',
  'author_id': 4,
  'description': 'Velit magni quidem non totam quia ipsum.',
  'gametype': {'id': 1, 'name': 'Minesweeper'},
  'id': 4,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'minus',
  'versions': [{'compiled': False,
                'extras': [],
                'frozen': False,
                'id': 1,
                'qualified': False}]},
 {'author': 'bschuster',
  'author_id': 2,
  'description': 'Et dolor recusandae delectus ut sapiente.',
  'gametype': {'id': 1, 'name': 'Minesweeper'},
  'id': 5,
  'lang': {'id': 2,
           'name': 'Java',
           'url': 'https://www.java.com/?isthaesslig=1'},
  'name': 'perspiciatis',
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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


Body:
```
[{'admin': False,
  'ais': [{'author': 'ahermiston',
           'author_id': 1,
           'description': 'Ut et asperiores delectus nihil iste.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'magnam',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 1,
  'name': 'ahermiston'},
 {'admin': False,
  'ais': [{'author': 'bschuster',
           'author_id': 2,
           'description': 'Et dolor recusandae delectus ut sapiente.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'perspiciatis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 2,
  'name': 'bschuster'},
 {'admin': False,
  'ais': [{'author': 'gberge',
           'author_id': 3,
           'description': 'Quae est itaque aliquid aut dicta qui et.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 3,
                    'name': 'Brainfuck',
                    'url': 'https://esolangs.org/wiki/Brainfuck'},
           'name': 'quisquam',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 3,
  'name': 'gberge'},
 {'admin': False,
  'ais': [{'author': 'winford21',
           'author_id': 4,
           'description': 'Velit magni quidem non totam quia ipsum.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'minus',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 4,
  'name': 'winford21'},
 {'admin': False,
  'ais': [{'author': 'erasmo59',
           'author_id': 5,
           'description': 'Quas tempore itaque commodi dolorem voluptatem.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 3,
                    'name': 'Brainfuck',
                    'url': 'https://esolangs.org/wiki/Brainfuck'},
           'name': 'repudiandae',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 5,
  'name': 'erasmo59'},
 {'admin': True, 'ais': [], 'id': 6, 'name': 'admin'}]
```



## GET: /api/games
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


Body:
```
[{'ais': [{'author': 'ahermiston',
           'author_id': 1,
           'description': 'Ut et asperiores delectus nihil iste.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'magnam',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'bschuster',
           'author_id': 2,
           'description': 'Et dolor recusandae delectus ut sapiente.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'perspiciatis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 1,
  'type': {'id': 1, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'ahermiston',
           'author_id': 1,
           'description': 'Ut et asperiores delectus nihil iste.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 1,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'magnam',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'erasmo59',
           'author_id': 5,
           'description': 'Quas tempore itaque commodi dolorem voluptatem.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 3,
                    'name': 'Brainfuck',
                    'url': 'https://esolangs.org/wiki/Brainfuck'},
           'name': 'repudiandae',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 2,
  'type': {'id': 1, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'erasmo59',
           'author_id': 5,
           'description': 'Quas tempore itaque commodi dolorem voluptatem.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 2,
           'lang': {'id': 3,
                    'name': 'Brainfuck',
                    'url': 'https://esolangs.org/wiki/Brainfuck'},
           'name': 'repudiandae',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'gberge',
           'author_id': 3,
           'description': 'Quae est itaque aliquid aut dicta qui et.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 3,
                    'name': 'Brainfuck',
                    'url': 'https://esolangs.org/wiki/Brainfuck'},
           'name': 'quisquam',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 3,
  'type': {'id': 1, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'gberge',
           'author_id': 3,
           'description': 'Quae est itaque aliquid aut dicta qui et.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 3,
           'lang': {'id': 3,
                    'name': 'Brainfuck',
                    'url': 'https://esolangs.org/wiki/Brainfuck'},
           'name': 'quisquam',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'winford21',
           'author_id': 4,
           'description': 'Velit magni quidem non totam quia ipsum.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'minus',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 4,
  'type': {'id': 1, 'name': 'Minesweeper'}},
 {'ais': [{'author': 'winford21',
           'author_id': 4,
           'description': 'Velit magni quidem non totam quia ipsum.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 4,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'minus',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]},
          {'author': 'bschuster',
           'author_id': 2,
           'description': 'Et dolor recusandae delectus ut sapiente.',
           'gametype': {'id': 1, 'name': 'Minesweeper'},
           'id': 5,
           'lang': {'id': 2,
                    'name': 'Java',
                    'url': 'https://www.java.com/?isthaesslig=1'},
           'name': 'perspiciatis',
           'versions': [{'compiled': False,
                         'extras': [],
                         'frozen': False,
                         'id': 1,
                         'qualified': False}]}],
  'id': 5,
  'type': {'id': 1, 'name': 'Minesweeper'}}]
```
Anonyme API Funktionen von bestimmten Objekten:



## GET: /api/ai/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


Body:
```
{'author': 'ahermiston',
 'author_id': 1,
 'description': 'Ut et asperiores delectus nihil iste.',
 'gametype': {'id': 1, 'name': 'Minesweeper'},
 'id': 1,
 'lang': {'id': 2,
          'name': 'Java',
          'url': 'https://www.java.com/?isthaesslig=1'},
 'name': 'magnam',
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
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


Body:
```
{'admin': False,
 'ais': [{'author': 'ahermiston',
          'author_id': 1,
          'description': 'Ut et asperiores delectus nihil iste.',
          'gametype': {'id': 1, 'name': 'Minesweeper'},
          'id': 1,
          'lang': {'id': 2,
                   'name': 'Java',
                   'url': 'https://www.java.com/?isthaesslig=1'},
          'name': 'magnam',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]}],
 'id': 1,
 'name': 'ahermiston'}
```



## GET: /api/game/1
----------

#### Request:

Headers:

 - Accept: */*
 - Accept-Encoding: gzip, deflate
 - Connection: keep-alive
 - Cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4
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
 - date: Tue, 02 Jun 2015 11:56:39 GMT
 - server: Werkzeug/0.10.4 Python/3.4.3
 - set-cookie: session=eyJfZmxhc2hlcyI6W3siIHQiOlsicG9zaXRpdmUiLCJEdSBoYXN0IGRpY2ggZWluZ2Vsb2dndC4iXX1dLCJfaWQiOiIzYTUwNDVhODYyZDkyZTVjNzJiODZkNTVlMmJiOGM5MCJ9.CE8rdw.5WQzxkHAM6Bylr4mDdnTRrN0ps4; HttpOnly; Path=/


Body:
```
{'ais': [{'author': 'ahermiston',
          'author_id': 1,
          'description': 'Ut et asperiores delectus nihil iste.',
          'gametype': {'id': 1, 'name': 'Minesweeper'},
          'id': 1,
          'lang': {'id': 2,
                   'name': 'Java',
                   'url': 'https://www.java.com/?isthaesslig=1'},
          'name': 'magnam',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]},
         {'author': 'bschuster',
          'author_id': 2,
          'description': 'Et dolor recusandae delectus ut sapiente.',
          'gametype': {'id': 1, 'name': 'Minesweeper'},
          'id': 5,
          'lang': {'id': 2,
                   'name': 'Java',
                   'url': 'https://www.java.com/?isthaesslig=1'},
          'name': 'perspiciatis',
          'versions': [{'compiled': False,
                        'extras': [],
                        'frozen': False,
                        'id': 1,
                        'qualified': False}]}],
 'id': 1,
 'type': {'id': 1, 'name': 'Minesweeper'}}
```
