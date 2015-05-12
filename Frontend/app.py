from flask import Flask, got_request_exception
from flask.ext.sqlalchemy import SQLAlchemy


from api import api, login_manager
from anonymous import anonymous
from profile import profile
from database import db, populate, AI
from _cfg import env
from time import time

import arrow


print("\n"*2 + "-"*36)
print("Turnierserver - Frontend - ", arrow.utcnow().to('local').format("HH:mm:ss"))
print("-"*36 + "\n"*2)

app = Flask("Turnierserver - Frontend")
app.secret_key = env.secret_key
login_manager.init_app(app)
app.config['SQLALCHEMY_DATABASE_URI'] = env.db_url
app.register_blueprint(api)
app.register_blueprint(anonymous)
app.register_blueprint(profile)

if env.airbrake:
	#airbrake.io
	print("Initializing Airbrake")
	import airbrake
	airbrake_logger = airbrake.getLogger(api_key=env.airbrake_key, project_id=env.airbrake_id)
	def log_exception(sender, exception, **extra):
		airbrake_logger.exception(exception, extra=extra)
	got_request_exception.connect(log_exception, app)


print("Connecting to", env.db_url)
db.init_app(app)
with app.app_context():
	db.drop_all()
	populate()


app.run(host="0.0.0.0", port=80, debug=True)