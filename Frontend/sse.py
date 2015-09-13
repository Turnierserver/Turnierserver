####################################################################################
# sse.py
#
# Copyright (C) 2015 Pixelgaffer
#
# This work is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as published by the
# Free Software Foundation; either version 2 of the License, or any later
# version.
#
# This work is distributed in the hope that it will be useful, but without
# any warranty; without even the implied warranty of merchantability or
# fitness for a particular purpose. See version 2 and version 3 of the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
####################################################################################
from flask import Response, current_app, stream_with_context
from functools import wraps


# SSE "protocol" is described here: http://mzl.la/UPFyxY
class ServerSentEvent(object):

	def __init__(self, data, event=None, id=None):
		self.data = str(data)
		self.event = event
		self.id = id

	def encode(self):
		s = ""
		if self.event:
			s += "event: " + self.event + "\n"
		if self.id:
			s += "id: " + str(self.id) + "\n"
		for line in self.data.split("\n"):
			s += "data: " + line + "\n"
		s += "\n"
		#print(s)
		return s

def sse_stream(meth):
	@wraps(meth)
	def wrapper(*args, **kwargs):
		def gen():
			id = 0
			for resp in meth(*args, **kwargs):
				if isinstance(resp, tuple):
					data, event = resp
				else:
					data = resp
					event = None
				yield ServerSentEvent(data=data, event=event, id=id).encode()
				id += 1
				#yield from meth(*args, **kwargs)
			else:
				yield ServerSentEvent(data="stream_stopped", event="stream_stopped", id=id).encode()
		return Response(stream_with_context(gen()), mimetype="text/event-stream")
	return wrapper


if __name__ == "__main__":
	import time
	from flask import Flask
	app = Flask(__name__)

	# Client code consumes like this.
	@app.route("/")
	def index():
		debug_template = """
		<html>
			<head>
			</head>
			<body>
				<h1>Server sent events</h1>
				<div id="event"></div>
				<script type="text/javascript">

				var eventOutputContainer = document.getElementById("event");
				var evtSrc = new EventSource("/subscribe");

				evtSrc.onmessage = function(e) {
					console.log(e.data);
					eventOutputContainer.innerHTML = e.data;
				};

				evtSrc.addEventListener("status", function(e) {
					console.log("status:", e.data)
				})

				evtSrc.addEventListener("stream_stopped", function (e) {
					console.log(e);
					console.log("stream_stopped")
					evtSrc.close()
				});

				</script>
			</body>
		</html>
		"""
		return(debug_template)


	@app.route("/subscribe")
	@sse_stream
	def subscribe():
		for i in range(5):
			yield time.time()
			yield ('swag', 'status')
			time.sleep(1)
		print("streams ending")
	app.debug = True
	app.run()
