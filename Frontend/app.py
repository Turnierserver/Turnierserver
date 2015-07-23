import arrow
print("\n"*2 + "-"*36)
print("Turnierserver - Frontend - ", arrow.utcnow().to('local').format("HH:mm:ss"))
print("-"*36 + "\n"*2)

from flask import Flask, got_request_exception
from werkzeug.contrib.fixers import ProxyFix
from flask.ext.login import current_user
from flask.ext.script import Manager
from flask.ext.migrate import Migrate, MigrateCommand

from commons import cache


from api import api, login_manager
from views.anonymous import anonymous_blueprint
from views.authenticated import authenticated_blueprint
from database import db, refresh_session, GameType
from backend import backend
from _cfg import env
from activityfeed import Activity
from errorhandling import handle_errors
from cli import manage

import time


app = Flask("Turnierserver - Frontend")
app.config.from_object("_cfg.env")
login_manager.init_app(app)

print("Connecting to", env.SQLALCHEMY_DATABASE_URI)
db.init_app(app)

migrate = Migrate(app, db)

manager = Manager(app)
manager.add_command('db', MigrateCommand)

app.jinja_env.add_extension("jinja2.ext.do")

app.register_blueprint(api)
app.register_blueprint(anonymous_blueprint)
app.register_blueprint(authenticated_blueprint)
handle_errors(app)

if env.airbrake:
	#airbrake.io
	print("Initializing Airbrake")
	import airbrake
	airbrake_logger = airbrake.getLogger(api_key=env.airbrake_key, project_id=env.airbrake_id)
	def log_exception(sender, exception, **extra):
		airbrake_logger.exception(exception, extra=extra)
	got_request_exception.connect(log_exception, app)

if True:
	# fix fuer Gunicorn und NGINX
	app.wsgi_app = ProxyFix(app.wsgi_app)


cache.init_app(app)
backend.app = app

@app.context_processor
def inject_globals():
	logged_in = False
	if current_user:
		if current_user.is_authenticated():
			logged_in = True

	current_gametype = GameType.selected(None, latest_on_none=True)
	return dict(env=env, logged_in=logged_in, current_gametype=current_gametype,
		latest_gametype=GameType.latest(), gametypes=GameType.query.all())


db_session_timeout = time.time()

@app.before_request
def refresh_db_session():
	global db_session_timeout
	if time.time() > db_session_timeout + 60*5:
		db_session_timeout = time.time()
		refresh_session()

@manager.command
def run():
	"Startet den Server."
	app_run_params = dict(host="::", port=env.web_port, threaded=True)
	if env.ssl:
		import ssl
		context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
		context.load_cert_chain('server.crt', 'server.key')
		app_run_params["ssl_context"] = context
	app.run(**app_run_params)

manage(manager, app)

Activity("Serverstart abgeschlossen...", extratext="Hier gehts los.\nAlle vorherigen Events sollten nicht wichtig sein.")

if __name__ == '__main__':
	manager.run()
