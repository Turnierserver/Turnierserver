from _cfg import env
import socket
import json
import sys
import time
import threading
from queue import Queue, Empty
from weakref import WeakSet
from database import db, Game, AI
from logger import logger

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
		self.latest_request_id = 0
		self.app = None

	def is_connected(self):
		if self.sock:
			return self.connected
		else:
			return False

	def connect(self):
		logger.info('Verbinde zum Backend @ {}:{}'.format(env.backend_url, env.backend_port))
		try:
			self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.sock.connect((env.backend_url, env.backend_port))
			self.sock.sendall(b"")
			self.connected = True
		except socket.error as e:
			logger.warning(e)
			self.sock = None
			self.connected = False

	def request_compile(self, ai):
		if ai.latest_version().frozen:
			logger.error("request_compile mit freigegebener KI aufgerufen!")
		reqid = self.latest_request_id
		self.latest_request_id += 1
		d = {
			'action': 'compile', 'id':str(ai.id)+'v'+str(ai.latest_version().version_id),
			'requestid': reqid, 'gametype': ai.type.id, 'language': ai.latest_version().lang.name
		}
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		logger.info("Backend [{}]: Kompilierung von {} gestartet".format(reqid, ai.name))
		return reqid


	def compile(self, ai):
		reqid = self.request_compile(ai)
		yield ("compiling", "status")
		yield ("F: Kompilierung mit ID {} angefangen.\n".format(reqid), "set_text")

		timed_out = 0
		char_before_timeout = None
		while True:
			resp = backend.lock_for_req(reqid, timeout=1)
			b_req = backend.request(reqid)
			if not resp:
				yield (".", "log")
				timed_out += 1
				if timed_out > 60:
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
						ai.latest_version().compiled = True
						db.session.commit()
					else:
						ai.latest_version().compiled = False
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
		reqid = self.latest_request_id
		self.latest_request_id += 1
		if any([ai.type != ais[0].type for ai in ais]):
			raise RuntimeError("AIs haben verschiedene Typen: " + str(ais))
		d = {'action': 'start', 'ais': [], 'languages': [], 'gametype': ais[0].type.id, 'requestid': reqid}
		for ai in ais:
			if not ai.latest_qualified_version():
				logger.debug(ais)
				logger.debug(ai)
				raise RuntimeError("Nich qualifizierte KI in request_game()")
			d['ais'].append(str(ai.id) + 'v' + str(ai.latest_qualified_version().version_id))
			d['languages'].append(ai.latest_qualified_version().lang.name)
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		self.requests[reqid]["queues"] = WeakSet()
		self.requests[reqid]["ai0"] = ais[0]
		self.requests[reqid]["ai1"] = ais[1]
		self.requests[reqid]["ai_objs"] = ais
		self.requests[reqid]["states"] = []
		self.requests[reqid]["status_text"] = "In Wartschlange"
		logger.info("Backend[{}]: Spiel mit {} gestartet".format(reqid, [ai.name for ai in ais]))
		return reqid

	def request_qualify(self, ai):
		if ai.latest_version().frozen:
			logger.error("request_qualify mit freigegebener KI aufgerufen!")
		reqid = self.latest_request_id
		self.latest_request_id += 1
		d = {'action': 'qualify', 'id': str(ai.id)+'v'+str(ai.latest_version().version_id),
			'gametype': ai.type.id, "language": ai.latest_version().lang.name, "requestid": reqid}
		self.requests[reqid] = d
		self.send_dict(d)
		self.requests[reqid]["queue"] = Queue()
		self.requests[reqid]["queues"] = WeakSet()
		self.requests[reqid]["ai0"] = ai
		self.requests[reqid]["ai1"] = AI.query.get(-ai.type.id)
		self.requests[reqid]["ai_objs"] = [ai, AI.query.get(-ai.type.id)]
		self.requests[reqid]["states"] = []
		self.requests[reqid]["crashes"] = []
		logger.info("Backend[{}]: Quali-Spiel mit '{}' gestartet".format(reqid, ai.name))
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
			logger.warning("Invalid Response!")
			pprint(d)
			return

		reqid = d["requestid"]

		if not reqid in self.requests:
			logger.warning("Requestid isnt known ({})".format(reqid))
			pprint(d)
			return

		#logger.info("Backend [{}]: {}".format(reqid, d))
		pprint(d)

		if "isCrash" in d and d["isCrash"]:
			if "queues" in self.requests[reqid]:
				for q in self.requests[reqid]["queues"]:
					q.put(d)
			self.requests[reqid]["crashes"].append(d)
			return

		self.requests[reqid].update(d)
		self.requests[reqid]["queue"].put(d)

		if self.requests[reqid]["action"] in ["start", "qualify"]:
			if "success" in d and self.requests[reqid]["action"] == "start":
				if not self.app:
					raise RuntimeError("Spiel vor verbindung mit App")
				with self.app.app_context():
					logger.info("game finished!")
					g = Game.from_inprogress(self.requests[reqid])
					logger.debug(g)
					self.requests[reqid]["finished_game_obj"] = g
					#pprint(self.requests[reqid])

			if "data" in d:
				d["data"]["calculationPoints"] = d["calculationPoints"]
				self.requests[reqid]["states"].append(d["data"])

			if "status" in d and d["status"] == "restarted":
				self.requests[reqid]["states"] = []

			for q in self.game_update_queues:
				q.put(d)

		if "queues" in self.requests[reqid]:
			for q in self.requests[reqid]["queues"]:
				q.put(d)


	def request(self, reqid):
		if reqid in self.requests:
			return self.requests[reqid]
		logger.warning("request for id " + str(reqid) + " failed!")

	def lock_for_req(self, reqid, timeout=30):
		try:
			return self.requests[reqid]["queue"].get(timeout=timeout)
		except Empty:
			logger.debug("TIMEOUT FOR " + str(reqid))
			return False

	def subscribe_game_update(self):
		logger.debug("New SSe")
		logger.debug(len(self.game_update_queues))
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
			#if not r["status"] in ["processed", "started"]:
			#	continue

			if "success" in r:
				continue

			games.append(dict(
				id=r["requestid"],
				ai0=r["ai0"],
				ai1=r["ai1"],
				status=r["status_text"],
				inqueue=r["status"] == "processed"
			))
		return games


	def inprogress_log(self, id):
		if not id in self.requests:
			return False
		if not "queues" in self.requests[id]:
			return False

		for d in self.requests[id]["states"]:
			yield d, "state"

		q = Queue()
		self.requests[id]["queues"].add(q)
		while True:
			try:
				update = q.get(timeout=120)
				d = self.request(id)
				if "progress" in update:
				    update["data"]["progress"] = update["progress"]
				if "data" in update:
					yield update["data"], "state"
				elif "success" in update:
					yield update, "success"
				elif "isCrash" in d and d["isCrash"]:
					yield d, "crash"
				else:
					logger.debug("no data in frame. " + str(update))
				if "finished_game_obj" in d:
					yield (d["finished_game_obj"], "finished_game_obj")
			except Empty:
				return



	def run(self):
		logger.info("Backend Thread running!")
		self.listen()

	def listen(self):
		while 1:
			if not self.connected:
				self.connect()
			if self.connected:
				r = self.sock.recv(1024*1024).decode("utf-8")
				if r == '':
					time.sleep(10)
					continue
				logger.debug('recvd ' + r)
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
				logger.debug("No connection to Backend...")
				time.sleep(3*60)


backend = Backend()
