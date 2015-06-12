import sys
import socket
import json
from io import StringIO
from importlib import import_module
from game_wrapper import GameWrapper
from pprint import pprint

URL = "127.0.0.1"
PORT = 1337

def properties_to_dict(s):
	d = {}
	for line in s.split("\n"):
		if not line.startswith("#") and len(line) > 2:
			key = line.split("=")[0]
			val = "=".join(line.split("=")[1:])
			if val.isdigit():
				val = int(val)
			if val in ["true", "false"]:
				val = val == "true"
			d[key] = val
	return d

class Rerouted_Output():
	def __init__(self):
		"""Stream Durcheinander"""
		self.buffer = StringIO()
		w_old = sys.stdout.write
		w_new = self.buffer.write
		def new_write(msg):
			w_old(msg)
			w_new(msg)
		sys.stdout = self.buffer
		sys.stderr = self.buffer
		self.buffer.write = new_write

	def read(self):
		return self.buffer.getvalue()

	def clear(self):
		self.buffer.seek(0)
		self.buffer.truncate()


class AIWrapper:
	def __init__(self, cls, properties):
		self.props = properties
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.ai = cls()
		self.output = Rerouted_Output()

	def connect(self):
		addr = (self.props["turnierserver.worker.host"], self.props["turnierserver.worker.server.port"])
		self.sock.connect(addr)
		print("Sende handschütteln")
		self.send(self.props["turnierserver.worker.server.aichar"] + self.props["turnierserver.ai.uuid"])

	def send(self, s):
		self.sock.sendall(bytes(s + "\n", "utf-8"))

	def run(self):
		self.connect()
		while True:
			r = self.sock.recv(1024*1024).decode("utf-8")
			print("Empfangen:", r)
			updates = json.loads(r)
			pprint("Geparsed:", updates)
			resp = self.update(self.getState(updates))
			pprint("Antwort:", resp)
			if resp:
				self.send(json.dumps(resp))
				print("Gesendet.")

	def getState(self, updates):
		"""In dieser Methode werden die empfangenen Daten zu einem Zustand verarbeitet."""
		raise NotImplementedError()

	def update(self, zustand):
		"""Diese Methode wird aufgerufen, wenn der Server ein Zustands-Update sendet."""
		raise NotImplementedError()

	def surrender(self):
		"""ACHTUNG: Mit dieser Methode gibt die KI auf"""
		self.send("SURRENDER")
		raise RuntimeError("SURRENDERED")


if __name__ == '__main__':
	print(sys.argv)
	# __name__ aifile propfile
	with open(sys.argv[2], "r") as f:
		props = properties_to_dict(f.read())
	print("properties:")
	pprint(props)
	if not "filename" in props:
		raise RuntimeError("No filename specified.")
	usermodule = import_module(props["filename"])
	print(usermodule)
	pprint(vars(usermodule))
	gw = GameWrapper(usermodule.AI, props)
	gw.run()
