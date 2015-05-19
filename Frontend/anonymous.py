from flask import Blueprint, render_template, abort
from flask.ext.login import current_user
from database import AI, User, Game
from activityfeed import Activity
import json

anonymous = Blueprint("anonymous", __name__)

@anonymous.route("/")
def index():
	return render_template("base.html")

@anonymous.route("/ai_list")
def ai_list():
	ais = AI.query.all()
	columns = [ais[i:i+3] for i in range(0, len(ais), 3)]
	return render_template("ai_list.html", columns=columns)

@anonymous.route("/ai/<int:id>")
def ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	return render_template("ai.html", ai=ai)

@anonymous.route("/user_list")
def user_list():
	return render_template("user_list.html", user_list=User.query.all())

@anonymous.route("/user/<int:id>")
def user(id):
	user = User.query.get(id)
	if not user:
		abort(404)
	return render_template("user.html", user=user)

@anonymous.route("/game_list")
def game_list():
	return render_template("game_list.html", game_list=Game.query.all())

@anonymous.route("/game/<int:id>")
def game(id):
	game = Game.query.get(id)
	if not game:
		abort(404)

	return render_template(game.type.viz, game=game)

