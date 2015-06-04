from _cfg import env
import socket
import json
import time
from activityfeed import Activity
import threading
from queue import Queue, Empty
from weakref import WeakSet
from database import db, AI, Game

from pprint import pprint


class Backend(threading.Thread):
	daemon=True
	game_update_queues = WeakSet()
	def __init__(self):
		threading.Thread.__init__(self)
		self.sock = None
		self.connected = False
		self.connect()
		self.start()
		self.requests = {}
		self.lastest_request_id = 0
		self.app = None

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
			'requestid': reqid, 'gametype': ai.type.id
		}
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		Activity("Backend [{}]: Kompilierung von {} gestartet".format(reqid, ai.name))
		return reqid


	def request_game(self, ais):
		reqid = self.lastest_request_id
		self.lastest_request_id += 1
		if any([ai.type != ais[0].type for ai in ais]):
			raise RuntimeError("AIs haben verschiedene Typen: " + str(ais))
		d = {'action': 'start', 'ais': [], 'gametype': ais[0].type.id, 'requestid': reqid}
		for ai in ais:
			d['ais'].append(str(ai.id) + 'v' + str(ai.lastest_version().version_id))
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		self.requests[reqid]["ai0"] = ais[0]
		self.requests[reqid]["ai1"] = ais[1]
		Activity("Backend[{}]: Spiel mit {} gestartet".format(reqid, [ai.name for ai in ais]))
		return reqid

	def request_qualify(self, ai):
		reqid = self.lastest_request_id
		self.lastest_request_id += 1
		d = {'action': 'qualify', 'id': str(ai.id)+'v'+str(ai.lastest_version().version_id),
			'gametype': ai.type.id, "requestid": reqid}
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		Activity("Backend[{}]: Quali-Spiel mit '{}' gestartet".format(reqid, ai.name))
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
			print("Invalid Response!")
			pprint(d)
			return

		reqid = d["requestid"]

		if not reqid in self.requests:
			print("Requestid isnt known")
			return

		#Activity("Backend [{}]: {}".format(reqid, d))
		pprint(d)

		self.requests[reqid].update(d)
		self.requests[reqid]["queue"].put(d)

		if self.requests[reqid]["action"] == "start":
			#for q in self.game_update_queues:
			#	q.put(d)
			if "success" in d:
				if not self.app:
					raise RuntimeError("Spiel, vor verbindung mit App")
				with self.app.app_context():
					Game.from_inprogress(self.requests[reqid])

	def request(self, reqid):
		return self.requests[reqid]

	def lock_for_req(self, reqid, timeout=30):
		try:
			return self.requests[reqid]["queue"].get(timeout=timeout)
		except Empty:
			print("TIMEOUT FOR", reqid)
			return False

	def subscribe_game_update(self):
		print("New SSe")
		print(len(self.game_update_queues))
		q = Queue()
		self.game_update_queues.add(q)
		return q


	def inprogress_games(self):
		games = []
		for reqid in self.requests:
			r = self.requests[reqid]
			if not r["action"] == "start":
				continue
			if not "status" in r:
				continue
			if not r["status"] == "processed":
				continue

			if "success" in r:
				continue

			games.append(dict(
				id=r["requestid"],
				ai0=r["ai0"],
				ai1=r["ai1"],
				status="1/8392"
			))
		return games


	def run(self):
		Activity("Backend Thread running!")
		self.listen()

	def listen(self):
		while 1:
			if not self.connected:
				self.connect()
			if self.connected:
				r = self.sock.recv(1024*1024).decode("utf-8")
				#print('recvd', r)
				## zerstückelte blöcke?
				for d in r.split("\n"):
					if d == '':
						##
						continue
					if d == '\n':
						pass
					else:
						self.parse(json.loads(d))
			else:
				print("No connection to Backend...")
				time.sleep(60*15)
			time.sleep(0.5)


backend = Backend()