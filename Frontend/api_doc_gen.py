import requests
from jinja2 import Template
from pprint import pformat

baseurl = "http://localhost:5000"

template = Template("""


## {{reqtype}}: {{url}}
----------

#### Request:

Headers:

{% for header, content in reqheaders %} - {{header}}: {{content}}\n{% endfor %}

Body:
```
	{{response.request.body}}
```

#### Response:

Statuscode:
	{{response.status_code}}
Headers:

{% for header, content in respheaders %} - {{header}}: {{content}}\n{% endfor %}

Body:
```
{{respbody}}
```

""")

s = requests.Session()

class ReqTypes:
	POST = "POST"
	GET = "GET"

class Endpoint:
	def __init__(self, url, type=ReqTypes.GET, body=None):
		self.url = url
		self.type = type
		self.body = body

	def start(self):
		requrl = baseurl + self.url
		print(requrl)
		if self.type == ReqTypes.POST:
			if self.body:
				r = s.post(requrl, data=self.body)
			else:
				r = s.post(requrl)
		if self.type == ReqTypes.GET:
			r = s.get(requrl)

		respbody = r.content.decode("utf-8")
		try:
			if r.json():
				respbody = pformat(r.json())
		except ValueError:
			pass

		def headers(o):
			return [h for h in sorted(o.headers.items()) if not h[0] == "date"]

		return template.render(
			reqtype=self.type, url=self.url,
			response=r,
			reqheaders = headers(r.request),
			respheaders = headers(r),
			respbody = respbody
		)


endpoints = [
]

with open("api_docs.md", "w") as f:
	def w(o):
		if isinstance(o, Endpoint):
			f.write(o.start())
		else:
			f.write(o + "\n")


	w("PingPongPingPong")
	w(Endpoint("/api", ReqTypes.GET))

	w("Anmeldung und so:")
	w(Endpoint("/api/login", ReqTypes.POST, {"email": "admin@ad.min", "password": "admin"}))
	w(Endpoint("/api/loggedin", ReqTypes.POST))
	w(Endpoint("/api/logout", ReqTypes.POST))
	w(Endpoint("/api/loggedin", ReqTypes.POST))
	w("Anonyme API Funktionen (brauchen keine Authentifizierung):")
	w(Endpoint("/api/ais"))
	w(Endpoint("/api/ais/1"))
	w(Endpoint("/api/users"))
	w(Endpoint("/api/games"))
	w(Endpoint("/api/games/1"))
	w(Endpoint("/api/langs"))
	w(Endpoint("/api/gametypes"))
	w("Anonyme API Funktionen von bestimmten Objekten:")
	w(Endpoint("/api/ai/1"))
	w(Endpoint("/api/user/1"))
	w(Endpoint("/api/game/1"))

	w("Funkionen mit Authentifizierung")
	w(Endpoint("/api/login", ReqTypes.POST, {"email": "admin@ad.min", "password": "admin"}))
	w(Endpoint("/api/ai/3/update", ReqTypes.POST, {"name": "Top Keks", "description": "Top Keks", "lang": 1}))