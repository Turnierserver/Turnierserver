from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy

from api import api, login_manager
from anonymous import anonymous
from database import db, create_mock_objects, AI

app = Flask("Turnierserver - Frontend")
app.secret_key = "foobar"
login_manager.init_app(app)
app.config['SQLALCHEMY_DATABASE_URI'] = "sqlite:///test.db"
app.register_blueprint(api)
app.register_blueprint(anonymous)


db.init_app(app)
with app.app_context():
	db.drop_all()
	db.create_all()
	create_mock_objects()


app.run(host="0.0.0.0", port=80, debug=True)