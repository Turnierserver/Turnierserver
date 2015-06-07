import arrow
print("\n"*2 + "-"*36)
print("Turnierserver - Frontend - ", arrow.utcnow().to('local').format("HH:mm:ss"))
print("-"*36 + "\n"*2)

from flask import Flask, got_request_exception
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.login import current_user
from flask.ext.script import Manager
from flask.ext.migrate import Migrate, MigrateCommand

from commons import cache


from api import api, login_manager
from views.anonymous import anonymous_blueprint
from views.authenticated import authenticated_blueprint
from database import db, populate, AI, User, ftp
from backend import backend
from _cfg import env
from activityfeed import activity_feed, Activity
from errorhandling import handle_errors
from cli import manage
from time import time


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


cache.init_app(app)
backend.app = app

@app.context_processor
def inject_globals():
	logged_in = False
	if current_user:
		if current_user.is_authenticated():
			logged_in = True
	return dict(env=env, logged_in=logged_in)

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