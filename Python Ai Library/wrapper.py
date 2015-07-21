import sys
import socket
import json
from io import StringIO
from importlib import import_module
from pprint import pprint
from copy import deepcopy

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
		buf = self.buffer.getvalue()
		self.clear()
		return buf

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
			r = self.sock.recv(1024**2 * 64).decode("utf-8")
			if not r or r == "\n":
				continue
			try:
				updates = json.loads(r)
			except ValueError as e:
				print("Fehler beim json parsen!!!")
				print(r)
				print(e)
				continue
			print("Empfangen:")
			pprint(updates)
			resp = self.update(self.getState(updates))
			print("Antwort:")
			fakeresp = deepcopy(resp)
			self.del_output(fakeresp)
			pprint(fakeresp)
			self.add_output(resp, self.output.read())
			self.send(json.dumps(resp))

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

	def del_output(self, d):
		"""Diese Methode nimmt eine Antwort und entfernt das Output, um sie anzuzeigen."""
		raise NotImplementedError()
	
	def add_output(self, d, o):
		"""Diese Methode nimmt eine Antwort und Output und hängt das Output an die Antwort."""
		raise NotImplementedError()


if __name__ == '__main__':
	from game_wrapper import GameWrapper
	print(sys.argv)
	# __name__ aifile propfile
	with open(sys.argv[2], "r") as f:
		props = properties_to_dict(f.read())
	print("properties:")
	pprint(props)
	usermodule = import_module(".".join(sys.argv[1].split(".")[:-1]))
	if not hasattr(usermodule, "AI"):
		raise RuntimeError("No AI class in " + sys.argv[1])
	gw = GameWrapper(usermodule.AI, props)
	gw.run()
