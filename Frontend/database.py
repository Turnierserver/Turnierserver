from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.login import current_user
from flask import send_file, abort, request
from _cfg import env
from logger import logger
from io import BytesIO
from functools import wraps
from threading import RLock
from werkzeug.security import generate_password_hash, check_password_hash
import mail
import ftputil
import socket
import arrow
import json
import magic
import uuid
import os
import shutil
import urllib
import unittest.mock
import sqlalchemy.exc


def timestamp():
	return arrow.utcnow().timestamp


db = SQLAlchemy(session_options={"expire_on_commit": False})

def refresh_session():
	## TODO: ne bessere Art, die verbindung zur DB zu refreshen
	try:
		db.session.query(User).first()
	except sqlalchemy.exc.OperationalError:
		logger.debug("refreshed session.")

class SyncedFTPError(Exception):
	pass

class SyncedFTP:
	def __init__(self):
		self.cached = set()
		self.ftp_host = None
		self.lock = RLock()
		self.err = SyncedFTPError

	def connect(self):
		logger.info("Verbinde zum FTP @ " + env.ftp_url)
		self.ftp_host = ftputil.FTPHost(env.ftp_url, env.ftp_uname, env.ftp_pw, timeout=10)


	def failsafe_locked(self, meth):
		@wraps(meth)
		def wrapper(*args, **kwargs):
			@self.safe
			def f():
				return meth(*args, **kwargs)
			try:
				try:
					return f()
				except self.err:
					return f()
			except self.err:
				abort(404)
		return wrapper

	def safe(self, meth):
		@wraps(meth)
		def wrapper(*args, **kwargs):
			with self.lock:
				try:
					try:
						if not self.ftp_host:
							self.connect()
						return meth(*args, **kwargs)
					except (ftputil.error.FTPError, socket.error) as e:
						logger.warning(e)
						self.connect()
						raise e
				except (ftputil.error.FTPError, socket.error) as e:
					logger.warning(e)
					logger.info("SL_FTP_E "+str(e))
					raise self.err()
		return wrapper

	def send_file_failsafe(self, path):
		@self.failsafe_locked
		def f(path):
			return self.send_file(path)
		return f(path)

	def send_file(self, path, filename=None):
		logger.debug("Downloading from FTP: " + path)
		if not self.ftp_host.path.exists(path):
			logger.info("Datei '" + path + "' existiert auf dem FTP nicht.")
			abort(404)
		with self.ftp_host.open(path, "rb") as remote_obj:
			logger.debug(path)
			data = remote_obj.read()
			f = BytesIO(data)
			f.seek(0)
			return send_file(f, mimetype=magic.from_buffer(data, mime=True).decode("ascii"),
			                 as_attachment=filename is not None, attachment_filename=filename)

	def copy_tree(self, from_dir, to_dir, overwrite=True):
		logger.info("COPY_TREE " + from_dir + " " + to_dir + " " + str(overwrite))
		@self.safe
		def f():
			if overwrite:
				if self.ftp_host.path.isdir(to_dir):
					logger.debug("DEL: " + to_dir)
					self.ftp_host.rmtree(to_dir)
				logger.debug("MKDIR: " + to_dir)
				self.ftp_host.mkdir(to_dir)

			for root, dirs, files in self.ftp_host.walk(from_dir, topdown=True, followlinks=False):
				t_dir = to_dir + root[len(from_dir):] + "/"
				s_dir = root + "/"
				for d in dirs:
					logger.debug("MKDIR: " + t_dir + d)
					self.ftp_host.mkdir(t_dir+d)
				for f in files:
					logger.debug(s_dir+f + " -> " + t_dir+f)
					with self.ftp_host.open(s_dir+f, "r", encoding="utf-8") as source:
						with self.ftp_host.open(t_dir+f, "w", encoding="utf-8") as target:
							target.write(source.read())
			return True

		try:
			return f()
		except self.err:
			logger.warning("copy_tree failed!")
			return False

	def upload_tree(self, from_dir, to_dir, overwrite=True):
		@self.safe
		def f():
			if overwrite:
				if self.ftp_host.path.isdir(to_dir):
					logger.debug("DEL: " + to_dir)
					self.ftp_host.rmtree(to_dir)
				logger.debug("MKDIR: " + to_dir)
				self.ftp_host.mkdir(to_dir)

			for root, dirs, files in os.walk(from_dir, topdown=True, followlinks=False):
				t_dir = to_dir + root[len(from_dir):] + "/"
				s_dir = root + "/"
				for d in dirs:
					logger.debug("MKDIR: " + t_dir + d)
					self.ftp_host.mkdir(t_dir+d)
				for f in files:
					logger.debug(s_dir+f + " -> " + t_dir+f)
					self.ftp_host.upload(s_dir+f, t_dir+f)
			return True

		try:
			return f()
		except self.err:
			logger.warning("upload_tree failed!")
			return False


	def download_tree(self, from_dir, to_dir, overwrite=True):
		logger.info("DOWNLOAD_TREE " + from_dir + " " + to_dir)
		@self.safe
		def f():
			if overwrite:
				if os.path.isdir(to_dir):
					logger.debug("DEL: " + to_dir)
					shutil.rmtree(to_dir)
				logger.debug("MKDIR: " + to_dir)
				os.mkdir(to_dir)

			for root, dirs, files in self.ftp_host.walk(from_dir, topdown=True, followlinks=False):
				t_dir = to_dir + root[len(from_dir):] + "/"
				s_dir = root + "/"
				for d in dirs:
					logger.debug("MKDIR: " + t_dir + d)
					self.ftp_host.mkdir(t_dir+d)
				for f in files:
					logger.debug(s_dir+f + " -> " + t_dir+f)
					self.ftp_host.download(s_dir+f, t_dir+f)
			return True

		try:
			return f()
		except self.err:
			logger.warning("upload_tree failed!")
			return False


ftp = SyncedFTP()

def db_obj_init_msg(obj):
	logger.debug(str(obj) + " erschafft.")

class User(db.Model):
	__tablename__ = 't_users'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True, nullable=False)
	name = db.Column(db.String(50), unique=True, nullable=False)
	firstname = db.Column(db.Text)
	lastname = db.Column(db.Text)
	email = db.Column(db.Text, unique=True, nullable=False)
	pw_hash = db.Column(db.Text)
	ai_list = db.relationship("AI", order_by="AI.id", backref="User", cascade="all, delete, delete-orphan")
	admin = db.Column(db.Boolean, default=False)
	validation_code = db.Column(db.String(36), nullable=True)
	pw_reset_token = db.Column(db.String(36), nullable=True)
	name_public = db.Column(db.Boolean, default=True)

	def __init__(self, *args, **kwargs):
		super(User, self).__init__(*args, **kwargs)
		self.validation_code = str(uuid.uuid4())
		db_obj_init_msg(self)

	def validate(self, uuid):
		if self.validation_code == uuid:
			self.validation_code = None
			logger.info("sucessfully validated " + self.name)
			db.session.commit()
		return self.validated

	@property
	def validated(self):
		return self.validation_code == None

	@property
	def elo(self):
		ais = AI.query.filter(AI.user == self).filter(AI.type == GameType.selected()).all()
		if len(ais) == 0:
			return None
		return max([ai.elo for ai in ais])


	def send_validation_mail(self):
		return mail.send_validation(self)

	def send_password_reset(self):
		self.pw_reset_token = str(uuid.uuid4())
		db.session.commit()
		return mail.reset_password(self)


	def info(self):
		return {"id": self.id, "name": self.name, "ais": [ai.info() for ai in self.ai_list]}

	def can_access(self, obj):
		if isinstance(obj, AI):
			return obj in self.ai_list or self.admin
		elif isinstance(obj, User):
			return obj == self or self.admin
		elif isinstance(obj, unittest.mock.Mock):
			return self.admin
		else:
			raise RuntimeError("Invalid Type: "+str(type(obj)))

	def check_pw(self, password):
		if not self.pw_hash:
			return False
		return check_password_hash(self.pw_hash, password)

	def set_pw(self, password):
		self.pw_hash = generate_password_hash(password)

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<User(id={}, name={}, admin={})".format(self.id, self.name, self.admin)

	# Flask.Login zeugs
	is_authenticatedated = True
	is_active = True
	is_anonymous = False
	def get_id(self):
		return self.id


class AI_Game_Assoc(db.Model):
	__tablename__ = 't_ai_game_assocs'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	game_id = db.Column(db.Integer, db.ForeignKey('t_games.id'))
	game = db.relationship("Game", cascade="all, delete, delete-orphan", single_parent=True)
	ai_id = db.Column(db.Integer, db.ForeignKey('t_ais.id'))
	ai = db.relationship("AI")
	score = db.Column(db.Integer)
	position = db.Column(db.Integer)
	calculationPoints = db.Column(db.Integer)
	## TODO: rechenpunkte wirklich speichern

	def __repr__(self):
		return "<AI_Game_Assoc(game={}, ai={})".format(self.game.id, self.ai.name)


class AI(db.Model):
	__tablename__ = 't_ais'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	name = db.Column(db.Text, nullable=False)
	desc = db.Column(db.Text)
	last_modified = db.Column(db.Integer, default=timestamp, onupdate=timestamp)
	elo = db.Column(db.Float, default=1200)
	user_id = db.Column(db.Integer, db.ForeignKey('t_users.id'))
	user = db.relationship("User", backref=db.backref('t_ais', order_by=id), lazy="joined")
	lang_id = db.Column(db.Integer, db.ForeignKey('t_langs.id'))
	lang = db.relationship("Lang", backref=db.backref('t_ais', order_by=id), lazy="joined")
	type_id = db.Column(db.Integer, db.ForeignKey('t_gametypes.id'))
	type = db.relationship("GameType", backref=db.backref('t_ais', order_by=id), lazy="joined")
	version_list = db.relationship("AI_Version", primaryjoin="AI.id == AI_Version.ai_id", order_by="AI_Version.id", backref="AI", cascade="all, delete, delete-orphan", lazy="joined")
	game_assocs = db.relationship("AI_Game_Assoc", order_by="AI_Game_Assoc.game_id", cascade="all, delete, delete-orphan")
	_active_version_id = db.Column(db.Integer, db.ForeignKey("t_ai_versions.id"), nullable=True,)

	def __init__(self, *args, **kwargs):
		super(AI, self).__init__(*args, **kwargs)
		db.session.add(self)
		db.session.commit()
		self.latest_version()
		self.updated(True)

		@ftp.safe
		def f():
			path = "AIs/"+str(self.id)+"/icon.png"
			if not ftp.ftp_host.path.isfile(path):
				return
			ftp.ftp_host.remove(path)

		try:
			f()
		except ftp.err:
			logger.warning("Icon reset failed")

		self.copy_example_code()

		db_obj_init_msg(self)

	def info(self, versions=True):
		d =  {
			"id": self.id, "name": self.name,
			"author": self.user.name,
			"author_id": self.user.id,
			"description": self.desc, "lang": self.lang.info(),
			"gametype": self.type.info(),
			"elo": self.elo
		}
		if versions:
			d["versions"] = [v.info() for v in self.version_list]
		return d

	@ftp.failsafe_locked
	def icon(self):
		if ftp.ftp_host.path.isfile("AIs/"+str(self.id)+"/icon.png"):
			return ftp.send_file("AIs/"+str(self.id)+"/icon.png")
		else:
			return ftp.send_file("AIs/default.png")

	def latest_version(self):
		if len(self.version_list) == 0:
			return self.new_version()
		return self.version_list[-1]

	def latest_qualified_version(self):
		for v in self.version_list[::-1]:
			if v.qualified:
				return v

	def active_version(self):
		if self._active_version_id:
			return AI_Version.query.get(self._active_version_id)
		return self.latest_frozen_version() # als fallback

	def latest_frozen_version(self):
		for v in self.version_list[::-1]:
			if v.frozen and v.qualified and v.compiled:
				return v

	def new_version(self, copy_prev=True):
		try:
			self.ftp_sync()
		except ftp.err:
			logger.warning("ftp sync failed in AI.new_version")
		if any([not v.frozen for v in self.version_list]):
			return False
		self.version_list.append(AI_Version(version_id=len(self.version_list) + 1, lang=self.lang))
		if len(self.version_list) > 1 and copy_prev:
			## copy AI code from prev version...
			new_path = "AIs/{}/v{}".format(self.id, self.version_list[-1].version_id)
			self.version_list[-2].copy_code(new_path)
		db.session.commit() # store in db; sync ids and stuff
		try:
			self.ftp_sync()
		except ftp.err:
			logger.warning("ftp sync failed in AI.new_version")
		return self.version_list[-1]

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def updated(self, ftpsync=True):
		self.last_modified = timestamp()
		if ftpsync:
			try:
				self.ftp_sync()
			except ftp.err:
				logger.warning("Failed to sync " + self.name)
				return False
		return True

	@ftp.safe
	def ftp_sync(self):
		logger.info("FTP-Sync von " + self.name)
		bd = "AIs/"+str(self.id)
		if not ftp.ftp_host.path.isdir(bd):
			ftp.ftp_host.mkdir(bd)
		if not ftp.ftp_host.path.isdir(bd+"/bin"):
			ftp.ftp_host.mkdir(bd+"/bin")

		for version in self.version_list:
			version.sync_extras()

		with ftp.ftp_host.open(bd+"/language.txt", "w") as f:
			f.write(self.lang.name)

	def set_name(self, name):
		# nur Namen mit min einem 'normalen' Buchstaben
		if not any([32 < ord(c) < 127 for c in name]):
			return False
		name = name.replace("\n", " ")
		self.name = name
		return True

	def copy_example_code(self):
		if self.latest_version().frozen:
			self.new_version()
		self.latest_version().copiled = False
		self.latest_version().qualified = False
		db.session.commit()
		source_dir_base = "Games/{}/{}/example_ai".format(self.type.id, self.lang.name)
		target_dir_base = "AIs/{}/v{}".format(self.id, self.latest_version().version_id)
		ret = ftp.copy_tree(source_dir_base, target_dir_base)
		self.ftp_sync()
		return ret

	@classmethod
	def filtered(cls, gametype=None):
		query = db.session.query(cls).filter(cls.id >= 0)
		if not gametype:
			gametype = GameType.selected()

		return query.filter(cls.type == gametype)

	@classmethod
	def reset_all_elo(cls, gametype):
		for ai in cls.query.filter(cls.type == gametype):
			ai.elo = 1200
		db.session.commit()

	@property
	def rank(self):
		## Schlaues caching
		sub = db.session.query(AI.id, db.func.row_number().over(order_by=db.desc(AI.elo)).label('pos')).filter(AI.type == self.type).subquery()
		return db.session.query(sub.c.pos).filter(sub.c.id==self.id).scalar()

	def available_extras(self):
		return Library.query.filter(Library.lang == self.latest_version().lang)

	def __repr__(self):
		return "<AI(id={}, name={}, user_id={}, lang={}, type={}, modified={}>".format(
			self.id, self.name, self.user_id, self.lang.name, self.type.name, self.last_modified
		)

class AI_Version(db.Model):
	__tablename__ = 't_ai_versions'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	version_id = db.Column(db.Integer)
	ai_id = db.Column(db.Integer, db.ForeignKey('t_ais.id'))
	compiled = db.Column(db.Boolean, default=False)
	qualified = db.Column(db.Boolean, default=False)
	frozen = db.Column(db.Boolean, default=False)
	ai = db.relationship("AI", foreign_keys="AI_Version.ai_id", backref=db.backref('t_ai_versions', order_by=id))
	lang_id = db.Column(db.Integer, db.ForeignKey('t_langs.id'))
	lang = db.relationship("Lang")
	extras = db.relationship("Library", secondary="t_ai_versions_libraries_assoc")

	def __init__(self, *args, **kwargs):
		super(AI_Version, self).__init__(*args, **kwargs)
		db_obj_init_msg(self)

	def info(self):
		return {
			"id": self.version_id, "extras": self.extra_names(),
			"compiled": self.compiled, "qualified": self.qualified,
			"frozen": self.frozen
		}


	def delete(self):

		@ftp.safe
		def f():
			logger.info("removing AI_Version data...")
			ftp.ftp_host.rmtree(self.path)
		try:
			f()
		except ftp.err:
			logger.error("couldn't delete version data!")

		db.session.delete(self)
		db.session.commit()

	def copy_code(self, new_path):
		return ftp.copy_tree(self.path, new_path)

	@property
	def current(self):
		return self.ai.latest_version() == self

	@property
	def is_active(self):
		return self.ai.active_version() == self

	def freeze(self):
		self.frozen = True

	@ftp.safe
	def sync_extras(self):
		if not self.ai:
			logger.error("ai_version.ai is None; " + str(self.id))
			return
		if not ftp.ftp_host.path.isdir(self.path):
			ftp.ftp_host.mkdir(self.path)
		with ftp.ftp_host.open(self.path + "/libraries.txt", "w") as f:
			for lib in self.extras:
				f.write(lib.name + "\n")
			if len(self.extras) == 0:
				f.write("\n")

	def extra_names(self):
		return [extra.name for extra in self.extras]

	@property
	def path(self):
		return "AIs/{}/v{}".format(self.ai_id, self.version_id)

	def __repr__(self):
		return "<AI_Version(id={}, version_id={}, ai_id={}>".format(self.id, self.version_id, self.ai_id)

class Lang(db.Model):
	__tablename__ = "t_langs"
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	name = db.Column(db.Text)
	url = db.Column(db.Text)
	ace_name = db.Column(db.Text)
	ai_list = db.relationship("AI", order_by="AI.id", backref="Lang")
	libs = db.relationship("Library", backref="lang")

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
	status = db.Column(db.Text)
	type_id = db.Column(db.Integer, db.ForeignKey('t_gametypes.id'))
	type = db.relationship("GameType", backref=db.backref('t_games', order_by=id))
	reason = db.Column(db.Text)
	_log = db.Column(db.Text)
	_crashes = db.Column(db.Text)

	def __init__(self, *args, **kwargs):
		super(Game, self).__init__(*args, **kwargs)
		self.timestamp = timestamp()
		db_obj_init_msg(self)

	@property
	def ais(self):
		return [assoc.ai for assoc in self.ai_assocs]

	@property
	def log(self):
		return json.loads(self._log)

	@log.setter
	def log(self, log):
		self._log = json.dumps(log)

	@property
	def crashes(self):
		if not self._crashes:
			return []
		return json.loads(self._crashes)

	@crashes.setter
	def crashes(self, crashes):
		self._crashes = json.dumps(crashes)

	@property
	def moves(self):
		return len(self.log)

	def time(self, locale='de'):
		return arrow.get(self.timestamp).to('local').humanize(locale=locale)

	def info(self, versions=True):
		return {"id": self.id, "ais": [assoc.ai.info(versions) for assoc in self.ai_assocs],
				"type": self.type.info(), "scores": {assoc.ai.id: assoc.score for assoc in self.ai_assocs},
				"moves": self.moves, "reason": self.reason, "timestamp": self.timestamp, "timestr": self.time()}

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def update_ai_elo(self):
		## TODO: funkioniert nur bei Spielen mit 2 Spielern

		# > dann wäre das neue elo:
		# > R'_A = R_A + 32 * (S_A - 1 / (1 + 10 ^ ((R_B - R_A) / 400)))

		ai0_assoc = self.ai_assocs[0]
		ai1_assoc = self.ai_assocs[1]
		ai0 = ai0_assoc.ai
		ai1 = ai1_assoc.ai

		if ai0.user == ai1.user:
			logger.info("Spiel zwischen KIs vom selben Nutzer; wird nicht gewertet")
			return

		if ai0_assoc.position < ai1_assoc.position:
			ai0gewonnen = 1
		elif ai0_assoc.position > ai1_assoc.position:
			ai0gewonnen = 0
		else:
			ai0gewonnen = 0.5

		ai1gewonnen = 1 - ai0gewonnen

		ai0eloneu = ai0.elo + 32 * (ai0gewonnen - 1 / (1 + 10 ** ((ai1.elo - ai0.elo) / 400)))
		ai1eloneu = ai1.elo + 32 * (ai1gewonnen - 1 / (1 + 10 ** ((ai0.elo - ai1.elo) / 400)))
		ai0.elo = ai0eloneu
		ai1.elo = ai1eloneu
		db.session.commit()
		logger.info("ai elo updated")

	@classmethod
	def from_inprogress(cls, d):
		if "exception" in d:
			logger.warning("Game Exception! " + str(d["exception"]))
			return
		ais = [d["ai0"], d["ai1"]]
		g = Game(type=ais[0].type)
		g.log = d["states"]
		g.crashes = d["crashes"]
		if "reason" in d:
			g.reason = d["reason"]
		db.session.add(g)
		db.session.commit()
		g.ai_assocs = [AI_Game_Assoc(game_id=g.id, ai_id=ai.id) for ai in ais]
		db.session.add(g)
		db.session.commit()
		for ai, score in d["scores"].items():
			ai = AI.query.get(int(ai.split("v")[0]))
			AI_Game_Assoc.query.filter(AI_Game_Assoc.game == g).filter(AI_Game_Assoc.ai == ai).one().score = score
		for ai, position in d["position"].items():
			ai = AI.query.get(int(ai.split("v")[0]))
			AI_Game_Assoc.query.filter(AI_Game_Assoc.game == g).filter(AI_Game_Assoc.ai == ai).one().position = position
		g.update_ai_elo()
		db.session.add(g)
		db.session.commit()
		logger.info("neues Spiel " + str(g))
		return g

	@classmethod
	def filter_output(cls, chunk):
		## TODO: kein lookup jedes mal
		for ai in chunk["output"]:
			ai_id = ai.split("v")[0]
			if not (current_user and current_user.is_authenticated and current_user.can_access(AI.query.get(ai_id))):
				chunk["output"][ai] = ""

	@classmethod
	def filter_crash(cls, data):
		logger.warning(str(data))
		ai = AI.query.get(int(data["id"].split("v")[0]))
		if not ai:
			logger.error("crash on nonexistant ai")
			return False, None
		data.pop("isCrash", None)
		data.pop("requestid", None)
		if current_user and current_user.is_authenticated and current_user.can_access(ai):
			return True, data
		return False, None

	@classmethod
	def delete_all(cls):
		logger.warning("Deleting all Games.")
		for game in cls.query:
			game.delete()
		db.session.commit()
		logger.warning("Games deleted.")

	def __repr__(self):
		return "<Game(id={}, type={})>".format(self.id, self.type.name)

class Game_inprogress:
	ais = []
	status = "1/2378"

	def __init__(self, id, d=None):
		self.type = GameType.latest()
		self.timestamp = timestamp()
		self.id = id
		if d:
			self.ais = d["ai_objs"]
		else:
			raise RuntimeError("Invalid backend request.")

	def time(self, locale):
		return arrow.get(self.timestamp).to('local').humanize(locale=locale)

	def delete(self):
		pass

	def __repr__(self):
		return "<Game_inprogress(id={}, type={})>".format(self.id, self.type.name)

class GameType(db.Model):
	__tablename__ = 't_gametypes'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	name = db.Column(db.Text, nullable=False)
	games = db.relationship("Game", order_by="Game.id", backref="GameType", cascade="all, delete, delete-orphan")
	last_modified = db.Column(db.Integer, default=timestamp, onupdate=timestamp)

	def __init__(self, *args, **kwargs):
		super(GameType, self).__init__(*args, **kwargs)
		db_obj_init_msg(self)
		if not self.last_modified:
			self.last_modified = timestamp()

	@property
	def viz(self):
		return "vizs/" + self.name.lower().replace(" ", "") + ".html"


	@classmethod
	def latest(cls):
		return cls.query.order_by(cls.id.desc()).first()

	@classmethod
	def selected(cls, gametype=None, latest_on_none=True):
		if not gametype:
			if "gametype" in request.cookies:
				gt = urllib.parse.unquote(request.cookies["gametype"])
				gametype = GameType.query.filter(GameType.name.ilike(gt)).first()
		if not gametype and latest_on_none:
			gametype = cls.latest()
		return gametype

	def updated(self):
		self.last_modified = timestamp()
		db.session.commit()

	def info(self):
		return {"id": self.id, "name": self.name, "last_modified": self.last_modified}

	def delete(self):
		logger.info("Deleting " + str(self))
		for ai in AI.query.filter(AI.type == self):
			ai.delete()
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<GameType(id={}, name={})>".format(self.id, self.name)

AI_Lib_Assoc = db.Table('t_ai_versions_libraries_assoc', db.Model.metadata,
	db.Column('ai_version_id', db.Integer, db.ForeignKey('t_ai_versions.id')),
	db.Column('library_id', db.Integer, db.ForeignKey('t_libraries.id'))
)

class Library(db.Model):
	__tablename__ = 't_libraries'
	id = db.Column(db.Integer, primary_key=True, autoincrement=True)
	name = db.Column(db.Text, nullable=False)
	display_name = db.Column(db.Text, nullable=False)
	lang_id = db.Column(db.Integer, db.ForeignKey("t_langs.id"))

	def info(self):
		return {"id": self.id, "name": self.name, "display_name": self.display_name}

	def delete(self):
		logger.info("Deleting " + str(self))
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<Library(id={}, name={})>".format(self.id, self.name)



def populate():
	db.create_all()

	def db_save(o):
		db.session.add_all(o)
		db.session.commit()

	py = Lang(name="Python", ace_name="python", url="https://www.python.org")
	java = Lang(name="Java", ace_name="java", url="https://www.java.com")
	cpp = Lang(name="C++", ace_name="cpp", url="http://isocpp.org")
	langs = [py, java, cpp]
	db_save(langs)

	minesweeper = GameType(name="Minesweeper", viz="vizs/minesweeper.html")
	gametypes = [minesweeper]
	db_save(gametypes)

	admin = User(name="admin", admin=True, email="admin@ad.min")
	admin.set_pw("admin")
	admin.validate(admin.validation_code)

	db_save([admin])
