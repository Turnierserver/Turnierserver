import sys
import socket
import json
from io import StringIO

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


class AI:
	output = Rerouted_Output()

	def __init__(self, properties):
		print(properties)
		self._props = properties_to_dict(properties)
		print(self._props)
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

	def _connect(self):
		addr = (self._props["turnierserver.worker.host"], self._props["turnierserver.worker.server.port"])
		self.sock.connect(addr)
		print("Sende handschütteln")
		self._send(self._props["turnierserver.worker.server.aichar"] + self._props["turnierserver.ai.uuid"])

	def _send(self, s):
		self.sock.sendall(bytes(s + "\n", "utf-8"))

	def _run(self):
		self._connect()
		while True:
			r = self.sock.recv(1024*1024).decode("utf-8")
			print("Empfangen:", r)
			updates = json.loads(r)
			print("Geparsed:", updates)
			resp = self.update(self.getState(updates))
			print("Antwort:", resp)
			if resp:
				self._send(json.dumps(resp))
				print("Gesendet.")

	def surrender(self):
		"""ACHTUNG: Mit dieser Methode gibt die KI auf"""
		self._send("SURRENDER")
		raise RuntimeError("SURRENDERED")

	def update(self, zustand):
		"""Diese Methode wird aufgerufen, wenn der Server ein Zustands-Update sendet."""
		pass

if __name__ == '__main__':
	print(sys.argv)
	# __name__ aifile propfile
	with open(sys.argv[2], "r") as f:
		prop = f.read()
	ai = AI(prop)
	ai._run()
