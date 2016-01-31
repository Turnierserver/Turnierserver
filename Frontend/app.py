import arrow
from flask import Flask, got_request_exception
from werkzeug.contrib.fixers import ProxyFix
from werkzeug.serving import WSGIRequestHandler
from flask.ext.login import current_user
from flask.ext.script import Manager, Shell
from flask.ext.migrate import Migrate, MigrateCommand

from commons import cache
from logger import logger

logger.info("Starte Frontend")

from api import api, login_manager
from views.anonymous import anonymous_blueprint
from views.authenticated import authenticated_blueprint
from database import *
from backend import backend
from _cfg import env
from errorhandling import handle_errors
from cli import manage

import logging
import time
import json


app = Flask("Turnierserver - Frontend")
app.config.from_object("_cfg.env")
app.url_map.strict_slashes = False
login_manager.init_app(app)

db_uri = env.SQLALCHEMY_DATABASE_URI.split("@")
db_uri[0] = ":".join(db_uri[0].split(":")[:2] + ["******"])
db_uri = "@".join(db_uri)
logger.info("Connecting to " + db_uri)
db.init_app(app)

migrate = Migrate(app, db)

manager = Manager(app)
manager.add_command('db', MigrateCommand)

def _make_context():
	backend.suppress_connection_warnings = True
	return globals()
manager.add_command("shell", Shell(make_context=_make_context))

app.jinja_env.filters["escapejs"] = lambda val: json.dumps(val)
app.jinja_env.add_extension("jinja2.ext.do")

app.register_blueprint(api)
app.register_blueprint(anonymous_blueprint)
app.register_blueprint(authenticated_blueprint)
handle_errors(app)

if env.SENTRY:
	from raven.contrib.flask import Sentry
	import subprocess
	with open("../.git/refs/heads/master", "r") as f: head = f.read().rstrip("\n")
	dirty = subprocess.check_output(["git", "status", "--porcelain"]).decode("utf-8", "ignore").rstrip("\n").splitlines()
	logger.info("Initializing Sentry")
	class MySentry(Sentry):
		def before_request(self, *args, **kwargs):
			Sentry.before_request(self, *args, **kwargs)
			self.client.extra_context({
				"commit": head,
				"dirty": dirty,
			})
			self.client.tags_context({"isDirty": len(dirty) > 0})
	sentry = MySentry(app, logging=True, level=logging.ERROR)

if env.airbrake:
	with open("../.git/refs/heads/master", "r") as f: head = f.read()
	logger.info("Initializing Airbrake")
	import airbrake
	airbrake_logger = airbrake.getLogger(api_key=env.airbrake_key, project_id=env.airbrake_id)
	def log_exception(sender, exception, **extra):
		extra["commit"] = head
		airbrake_logger.exception(exception, extra=extra)
	got_request_exception.connect(log_exception, app)

# Client-IPs von NGINX übernehmen
app.wsgi_app = ProxyFix(app.wsgi_app)

def address_string(self):
	if not hasattr(self, "headers"):
		return self.client_address[0]
	forwarded_for = self.headers.get('X-Forwarded-For', '').split(',')
	if forwarded_for and forwarded_for[0]:
		return forwarded_for[0]
	else:
		return self.client_address[0]
WSGIRequestHandler.address_string = address_string



cache.init_app(app)
backend.app = app

@app.context_processor
def inject_globals():
	logged_in = False
	if current_user:
		if current_user.is_authenticated:
			logged_in = True

	current_gametype = GameType.selected(None, latest_on_none=True)
	return dict(env=env, logged_in=logged_in, current_gametype=current_gametype,
	            latest_gametype=GameType.latest(), gametypes=GameType.query.all())


db_session_timeout = time.time()

@app.before_request
def refresh_db_session():
	global db_session_timeout
	if time.time() > db_session_timeout + 60:
		db_session_timeout = time.time()
		refresh_session()

@manager.command
def run():
	"Startet den Server."

	if not Lang.query.first():
		logger.critical("Missing Lang(s) / GameType(s)")
		exit()

	app_run_params = dict(host="::", port=env.web_port, threaded=True)
	if env.DEBUG and not env.USE_RELOADER:
		app_run_params["use_reloader"] = False
	app.run(**app_run_params)

manage(manager, app)

logger.info("Module geladen")
if __name__ == '__main__':
	manager.run()
