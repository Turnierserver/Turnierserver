from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy

from api import api, login_manager
from anonymous import anonymous
from database import db, populate, AI
from _cfg import env

app = Flask("Turnierserver - Frontend")
app.secret_key = "foobar"
login_manager.init_app(app)
app.config['SQLALCHEMY_DATABASE_URI'] = env.db_url
app.register_blueprint(api)
app.register_blueprint(anonymous)

print("Connecting to", env.db_url)
db.init_app(app)
with app.app_context():
	db.drop_all()
	populate()


app.run(host="0.0.0.0", port=80, debug=True)