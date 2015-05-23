from _cfg import env
import socket
import json
import time
from activityfeed import Activity
import threading
from queue import Queue, Empty

class Backend(threading.Thread):
	daemon=True
	def __init__(self):
		threading.Thread.__init__(self)
		self.sock = None
		self.connected = False
		self.connect()
		self.start()
		self.requests = {}
		self.lastest_request_id = 0

	def is_connected(self):
		if self.sock:
			return self.connected
		else:
			return False

	def connect(self):
		a = Activity('Verbinde zum Backend @ {}:{}'.format(env.backend_url, env.backend_port))
		try:
			self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.sock.connect((env.backend_url, env.backend_port))
			self.connected = True
		except socket.error as e:
			print(e)
			a.extratext = str(e)
			self.sock = None
			self.connected = False

	def request_compile(self, ai):
		reqid = self.lastest_request_id
		self.lastest_request_id += 1
		d = {
			'action': 'compile', 'id':str(ai.id)+'v'+str(ai.lastest_version().version_id),
			'requestid': reqid, 'gametype': 1
		}
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		Activity("Backend [{}]: Kompilierung von {} gestartet".format(reqid, ai.name))
		return reqid


	def request_game(self, ais):
		reqid = self.lastest_request_id
		self.lastest_request_id += 1
		d = {'action': 'start', 'ais': [], 'gametype': 1, 'requestid': reqid}
		for ai, version in ais:
			d['ais'].append(str(ai.id) + 'v' + str(version.version_id))
		##gametype checkenreq
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		Activity("Backend[{}]: Spiel mit {} gestartet".format(reqid, [ai.name for ai, version in ais]))
		return reqid

	def send_dict(self, d):
		if not self.is_connected():
			self.connect()
		if self.is_connected():
			self.sock.sendall(bytes(json.dumps(d) + "\n", "utf-8"))
			return True
		else:
			return False

	def parse(self, d):
		if not "requestid" in d:
			print("Invalid Response!", d)
			return

		print(d)
		reqid = d["requestid"]

		if not reqid in self.requests:
			print("Requestid isnt known")
			return

		Activity("Backend [{}]: {}".format(reqid, d))

		self.requests[reqid].update(d)
		self.requests[reqid]["queue"].put(d)

	def lock_for_req(self, reqid, timeout=30):
		try:
			return self.requests[reqid]["queue"].get(timeout=timeout)
		except Empty:
			print("TIMEOUT FOR", reqid)
			return False

	def run(self):
		Activity("Backend Thread running!")
		self.listen()

	def listen(self):
		while 1:
			if not self.connected:
				self.connect()
			if self.connected:
				r = self.sock.recv(1024*10).decode("utf-8")
				print('recvd', r)
				if r == '':
					##
					break
				if r == '\n':
					pass
				else:
					self.parse(json.loads(r))
			else:
				print("No connection to Backend...")
				time.sleep(60*15)
			time.sleep(0.5)


backend = Backend()