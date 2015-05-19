from flask.ext.sqlalchemy import SQLAlchemy
from flask import send_file, abort
from _cfg import env
from activityfeed import Activity
from io import BytesIO
from functools import wraps
import ftputil
import socket
import arrow
import json


def timestamp():
	return arrow.utcnow().timestamp


db = SQLAlchemy()

class SyncedFTP:
	def __init__(self):
		self.cached = set()
		self.ftp_host = None

	def connect(self):
		Activity("Verbinde zum FTP @ " + env.ftp_url)
		self.ftp_host = ftputil.FTPHost(env.ftp_url, env.ftp_uname, env.ftp_pw, timeout=1)


	def failsafe(self, meth):
		@wraps(meth)
		def wrapper(*args, **kwargs):
			if not self.ftp_host:
				try:
					self.connect()
				except ftputil.error.FTPError as e:
					Activity("Verbindung zum FTP hat gefailed", extratext=str(e))
					abort(404)
			try:
				return meth(*args, **kwargs)
			except socket.error as e:
				Activity(e)
				self.connect()
				return meth(*args, **kwargs)
			except ftputil.error.FTPError as e:
				Activity(e)
				self.connect()
				return meth(*args, **kwargs)
		return wrapper

	def send_file(self, path):
		@self.failsafe
		def f(path):
			print("Downloading from FTP: ", path)
			if not self.ftp_host.path.exists(path):
				Activity("Datei '" + path + "' existiert auf dem FTP nicht.")
				abort(404)
			with self.ftp_host.open(path, "rb") as remote_obj:
				f = BytesIO(remote_obj.read())
				f.seek(0)
				return send_file(f)
		return f(path)

	def send_cached(self, path):
		pass


ftp = SyncedFTP()

def db_obj_init_msg(obj):
	import inspect, pprint
	callername = inspect.getouterframes(inspect.currentframe(), 2)[5][3]
	Activity(str(obj) + " erschafft.", extratext="Aufgerufen von '" + callername + "'.")

class User(db.Model):
	__tablename__ = 't_users'
	id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.String(50), unique=True, nullable=False)
	ai_list = db.relationship("AI", order_by="AI.id", backref="User", cascade="all, delete, delete-orphan", lazy="dynamic")
	admin = db.Column(db.Boolean, default=False)

	def __init__(self, *args, **kwargs):
		super(User, self).__init__(*args, **kwargs)
		db_obj_init_msg(self)

	def info(self):
		return {"id": self.id, "name": self.name, "ais": [ai.info() for ai in self.ai_list], "admin": self.admin}

	@ftp.failsafe
	def icon(self):
		if ftp.ftp_host.path.isfile("Users/"+str(self.id)+"/icon.png"):
			return ftp.send_file("Users/"+str(self.id)+"/icon.png")
		else:
			return ftp.send_file("Users/default.png")

	def can_access(self, ai):
		return ai in self.ai_list or self.admin

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<User(id={}, name={}, admin={})".format(self.id, self.name, self.admin)

	# Flask.Login zeugs
	def is_authenticated(self):
		return True
	def is_active(self):
		return True
	def is_anonymous(self):
		return False
	def get_id(self):
		return self.name


class AI_Game_Assoc(db.Model):
	__tablename__ = 't_ai_game_assocs'
	game_id = db.Column(db.Integer, db.ForeignKey('t_games.id'), primary_key=True)
	game = db.relationship("Game", cascade="all, delete, delete-orphan", single_parent=True)
	ai_id = db.Column(db.Integer, db.ForeignKey('t_ais.id'), primary_key=True)
	ai = db.relationship("AI")
	role_id = db.Column(db.Integer, db.ForeignKey('t_gametyperoles.id'), primary_key=True)
	role = db.relationship("GameTypeRole", backref="assocs")


class AI(db.Model):
	__tablename__ = 't_ais'
	id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.Text, nullable=False)
	desc = db.Column(db.Text)
	last_modified = db.Column(db.Integer, default=timestamp, onupdate=timestamp)
	user_id = db.Column(db.Integer, db.ForeignKey('t_users.id'))
	user = db.relationship("User", backref=db.backref('t_ais', order_by=id))
	lang_id = db.Column(db.Integer, db.ForeignKey('t_langs.id'))
	lang = db.relationship("Lang", backref=db.backref('t_ais', order_by=id))
	type_id = db.Column(db.Integer, db.ForeignKey('t_gametypes.id'))
	type = db.relationship("GameType", backref=db.backref('t_ais', order_by=id))
	version_list = db.relationship("AI_Version", order_by="AI_Version.id", backref="AI", cascade="all, delete, delete-orphan")
	game_assocs = db.relationship("AI_Game_Assoc", order_by="AI_Game_Assoc.game_id", cascade="all, delete, delete-orphan")

	def __init__(self, *args, **kwargs):
		super(AI, self).__init__(*args, **kwargs)
		self.lastest_version()
		db_obj_init_msg(self)

	def info(self):
		return {"id": self.id, "name": self.name, "author": self.user.name, "description": self.desc, "lang": self.lang.info(), "gametype": self.type.info()}

	@ftp.failsafe
	def icon(self):
		if ftp.ftp_host.path.isfile("AIs/"+str(self.id)+"/icon.png"):
			return ftp.send_file("AIs/"+str(self.id)+"/icon.png")
		else:
			return ftp.send_file("AIs/default.png")

	def lastest_version(self):
		if len(self.version_list) == 0:
			self.version_list.append(AI_Version())
		return self.version_list[-1]

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	@ftp.failsafe
	def ftp_sync(self):
		print("FTP-Sync von " + self.name)
		bd = "AIs/"+str(self.id)
		if not ftp.ftp_host.path.isdir(bd):
			ftp.ftp_host.mkdir(bd)
		if not ftp.ftp_host.path.isdir(bd+"/bin"):
			ftp.ftp_host.mkdir(bd+"/bin")

		for version in self.version_list:
			if not ftp.ftp_host.path.isdir(bd+"/v"+str(version.id)):
				ftp.ftp_host.mkdir(bd+"/v"+str(version.id))
			with ftp.ftp_host.open(bd+"/v"+str(version.id)+"/settings.prop", "w") as f:
				def write_prop(f, d):
					for key in d:
						f.write(key + "=" + str(d[key]) + "\n")
				f.write("#Vom Frontend durch Integrity-Checks generiert\n")
				write_prop(f, dict(
					language = self.lang.name,
					language_id = self.lang.id,
					name = self.name,
					id = self.id,
					author = self.user.name,
					type = self.type.name,
					type_id = self.type.id
				))


		with ftp.ftp_host.open(bd+"/language.txt", "w") as f:
			f.write(self.lang.name)

	def __repr__(self):
		return "<AI(id={}, name={}, user_id={}, lang={}, type={}>".format(self.id, self.name, self.user_id, self.lang.name, self.type.name)

class AI_Version(db.Model):
	__tablename__ = 't_ai_versions'
	id = db.Column(db.Integer, primary_key=True)
	ai_id = db.Column(db.Integer, db.ForeignKey('t_ais.id'))
	ai = db.relationship("AI", backref=db.backref('t_ai_versions', order_by=id))
	extras_str = db.Column(db.Text, default="[]")
	# dafuer komm ich in die DB-Hoelle

	def __init__(self, *args, **kwargs):
		super(AI_Version, self).__init__(*args, **kwargs)
		if not self.extras_str:
			# eigentlich wird der standart Wert schon oben gesetzt
			self.extras_str = "[]"
		db_obj_init_msg(self)

	def info(self):
		return {"id": self.id, "extras": self.extras()}

	def extras(self, e=False):
		if e:
			self.extras_str = json.dumps(e)
		return json.loads(self.extras_str)

	def __repr__(self):
		return "<AI_Version(id={}>".format(self.id)

class Lang(db.Model):
	__tablename__ = "t_langs"
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	name = db.Column(db.Text)
	url = db.Column(db.Text)
	ai_list = db.relationship("AI", order_by="AI.id", backref="Lang")

	def __init__(self, *args, **kwargs):
		super(Lang, self).__init__(*args, **kwargs)
		db_obj_init_msg(self)

	def info(self):
		return {"id": self.id, "name": self.name, "url": self.url}

	def __repr__(self):
		return "<Lang(id={}, name={}, url={}>".format(self.id, self.name, self.url)

class Game(db.Model):
	__tablename__ = 't_games'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	ai_assocs = db.relationship("AI_Game_Assoc", cascade="all, delete, delete-orphan")
	timestamp = db.Column(db.BigInteger)
	type_id = db.Column(db.Integer, db.ForeignKey('t_gametypes.id'))
	type = db.relationship("GameType", backref=db.backref('t_games', order_by=id))

	def __init__(self, *args, **kwargs):
		super(Game, self).__init__(*args, **kwargs)
		self.timestamp = arrow.utcnow().timestamp
		db_obj_init_msg(self)

	def time(self, locale):
		return arrow.get(self.timestamp).to('local').humanize(locale=locale)

	def info(self):
		return {"id": self.id, "ais": [assoc.ai.info() for assoc in self.ai_assocs], "type": self.type.info()}

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<Game(id={}, type={})>".format(self.id, self.type.name)



class GameType(db.Model):
	__tablename__ = 't_gametypes'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	name = db.Column(db.Text, nullable=False)
	viz = db.Column(db.Text, nullable=False)
	games = db.relationship("Game", order_by="Game.id", backref="GameType", cascade="all, delete, delete-orphan")
	roles = db.relationship("GameTypeRole", backref="GameType", cascade="all, delete, delete-orphan")

	def __init__(self, *args, **kwargs):
		super(GameType, self).__init__(*args, **kwargs)
		db_obj_init_msg(self)

	def info(self):
		return {"id": self.id, "name": self.name}

	def __repr__(self):
		return "<GameType(id={}, name={})>".format(self.id, self.name)

class GameTypeRole(db.Model):
	__tablename__ = 't_gametyperoles'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	name=db.Column(db.Text, nullable=False)
	gametype_id = db.Column(db.Integer, db.ForeignKey('t_gametypes.id'))


def populate(count=20):
	r = list(range(1, count+1))
	import random
	db.create_all()

	py = Lang(id=1, name="Python", url="https://www.python.org")
	java = Lang(id=2, name="Java", url="https://www.java.com/?isthaesslig=1")
	brainfuck = Lang(id=3, name="Brainfuck", url="https://esolangs.org/wiki/Brainfuck")
	langs = [py, java, brainfuck]

	minesweeper = GameType(id=1, name="Minesweeper", viz="vizs/minesweeper.html", roles=[
		GameTypeRole(id=1, name="builder"), GameTypeRole(id=2, name="solver")
	])
	gametypes = [minesweeper]

	users = [User(name="testuser"+str(i), id=i) for i in r]
	random.shuffle(users)
	ais = [AI(id=i, user=users[i-1], name="testai"+str(i), desc="Beschreibung", lang=py, type=minesweeper) for i in r]
	games = [Game(id=i, type=minesweeper) for i in r]
	assocs = []
	for ri, role in enumerate(minesweeper.roles, 1):
		assocs += [AI_Game_Assoc(game_id=games[i-1].id, ai_id=ais[i-ri].id, role=role) for i in r]

	users.append(User(name="admin", admin=True))

	db.session.add_all(users + ais + games + assocs + langs + gametypes)
	db.session.commit()

	#games[0].delete()

	#for ai in ais:
	#	ai.ftp_sync()


if __name__ == '__main__':
	populate(99)
	for user in db.query(User).all():
		print(user)
		print(user.info())