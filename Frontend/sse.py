from flask import Response, current_app, stream_with_context
from functools import wraps


# SSE "protocol" is described here: http://mzl.la/UPFyxY
class ServerSentEvent(object):
	def __init__(self, data, event=None, msg_id=None):
		self.data = str(data)
		self.event = event
		self.msg_id = msg_id

	def encode(self):
		s = ""
		if self.event:
			s += "event: " + str(self.event) + "\n"
		if self.msg_id:
			s += "id: " + str(self.msg_id) + "\n"
		for line in self.data.split("\n"):
			s += "data: " + line + "\n"
		s += "\n"
		#print(s)
		return s

def sse_stream(meth):
	@wraps(meth)
	def wrapper(*args, **kwargs):
		def gen():
			msg_id = 0
			gen = meth(*args, **kwargs)
			def f(resp):
				if isinstance(resp, tuple):
					data, event = resp
				else:
					data = resp
					event = None
				return ServerSentEvent(data=data, event=event, msg_id=msg_id).encode()
			try:
				yield f(next(gen))
				msg_id += 1
			except StopIteration as e:
				yield f(e.value)
			for resp in gen:
				yield f(resp)
				msg_id += 1
			else:
				yield ServerSentEvent(data="stream_stopped", event="stream_stopped", msg_id=msg_id).encode()
		return Response(stream_with_context(gen()), mimetype="text/event-stream")
	return wrapper
