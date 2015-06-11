from _cfg import env
import socket
import json
import time
from activityfeed import Activity
import threading
from queue import Queue, Empty
from weakref import WeakSet
from database import db, Game

from pprint import pprint


class Backend(threading.Thread):
	daemon = True
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


	def compile(self, ai):
		reqid = self.request_compile(ai)
		yield ("compiling", "status")
		yield ("F: Kompilierung mit ID {} angefangen.\n".format(reqid), "set_text")

		timed_out = 0
		char_before_timeout = None
		while True:
			resp = backend.lock_for_req(reqid, timeout=5)
			b_req = backend.request(reqid)
			if not resp:
				yield (".", "log")
				timed_out += 1
				if timed_out > 20:
					yield ("\nDas Backend sendet nichts.", "log")
					yield ("\nVersuch es nochmal.", "log")
					yield ("Das Backend sendet nichts.", "error")
					return
			else:
				if timed_out > 0:
					if char_before_timeout == "\n":
						yield ("\n", "log")
					else:
						yield (" ", "log")
				timed_out = 0
				if "success" in resp:
					if resp["success"]:
						yield ("Anfrage erfolgreich beendet\n", "log")
						ai.lastest_version().compiled = True
						db.session.commit()
					else:
						ai.lastest_version().compiled = False
						db.session.commit()
						yield ("Kompilierung fehlgeschlagen\n", "log")
						if "exception" in resp:
							yield (resp["exception"], "log")
						yield ("Kompilierung fehlgeschlagen", "error")
					return
				elif "status" in resp:
					if resp["status"] == "processed":
						yield ("Anfrage angefangen\n", "log")
				elif "compilelog" in resp:
					yield (resp["compilelog"], "log")
					if len(resp["compilelog"]) > 0:
						char_before_timeout = resp["compilelog"][-1]
				else:
					# Falls die Antwort vom Backend nicht verstanden wurde.
					yield ("B: " + str(resp) + "\n", "log")


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
		self.requests[reqid]["queues"] = WeakSet()
		self.requests[reqid]["ai0"] = ais[0]
		self.requests[reqid]["ai1"] = ais[1]
		self.requests[reqid]["ai_objs"] = ais
		self.requests[reqid]["states"] = []
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
			print("Requestid isnt known ({})".format(reqid))
			pprint(d)
			return

		#Activity("Backend [{}]: {}".format(reqid, d))
		pprint(d)

		self.requests[reqid].update(d)
		self.requests[reqid]["queue"].put(d)
		if "queues" in self.requests[reqid]:
			for q in self.requests[reqid]["queues"]:
				q.put(d)

		if self.requests[reqid]["action"] == "start":
			for q in self.game_update_queues:
				q.put(d)
			if "success" in d:
				if not self.app:
					raise RuntimeError("Spiel vor verbindung mit App")
				with self.app.app_context():
					Game.from_inprogress(self.requests[reqid])
					return

			if "data" in d:
				self.requests[reqid]["states"].append(d["data"])


	def request(self, reqid):
		if reqid in self.requests:
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
			if not r["status"] in ["processed", "started"]:
				continue

			if "success" in r:
				continue

			games.append(dict(
				id=r["requestid"],
				ai0=r["ai0"],
				ai1=r["ai1"],
				status="1/8392",
				inqueue=r["status"] == "processed"
			))
		return games


	def inprogress_log(self, id):
		if not id in self.requests:
			return False
		if not "queues" in self.requests[id]:
			return False

		for d in self.requests[id]["states"]:
			yield d

		q = Queue()
		self.requests[id]["queues"].add(q)
		while True:
			try:
				d = q.get(timeout=120)
				if "data" in d:
					yield d["data"]
				else:
					print("no data in frame.", d)
			except Empty:
				return



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
