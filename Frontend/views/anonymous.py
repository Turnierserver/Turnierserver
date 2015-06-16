from flask import Blueprint, render_template, abort, flash, url_for, request
from flask.ext.login import current_user
from database import AI, User, Game, GameType, Game_inprogress
from activityfeed import Activity
from backend import backend
import json

anonymous_blueprint = Blueprint("anonymous", __name__)

@anonymous_blueprint.route("/")
def index():
	return render_template("index.html")

@anonymous_blueprint.route("/ai_list")
def ai_list():
	ais = AI.filtered().all()
	columns = [ais[i:i+3] for i in range(0, len(ais), 3)]
	return render_template("ai_list.html", columns=columns)

@anonymous_blueprint.route("/ai/<int:id>")
def ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	return render_template("ai.html", ai=ai)

@anonymous_blueprint.route("/user_list")
def user_list():
	return render_template("user_list.html", user_list=User.query.all())

@anonymous_blueprint.route("/user/<int:id>")
def user(id):
	user = User.query.get(id)
	if not user:
		abort(404)
	return render_template("user.html", user=user)

@anonymous_blueprint.route("/game_list")
def game_list():
	query = Game.query.order_by(Game.id.desc())
	if "gametype" in request.cookies:
		gametype = GameType.query.filter(GameType.name.ilike(request.cookies["gametype"])).first()
		query = query.filter(Game.type == gametype)
	else:
		query = query.filter(Game.type == GameType.lastest())
	return render_template("game_list.html", game_list=query.all(), in_progress_games=backend.inprogress_games())

@anonymous_blueprint.route("/game/<int:id>")
def game(id):
	game = Game.query.get(id)
	if not game:
		abort(404)

	stream = url_for("api.game_log", id=id)

	return render_template(game.type.viz, game=game, inprogress=False, ai0=game.ais[0], ai1=game.ais[1], stream=stream)

@anonymous_blueprint.route("/game/inprogress/<int:id>")
def inprogress_game(id):
	## inpgrogress type
	if not backend.request(id):
		abort(404)
	if not backend.request(id)["action"] == "start":
		print("Invalid gameid!")
		abort(404)
	game = Game_inprogress(id, backend.request(id))

	stream = url_for("api.game_inprogress_log", id=game.id)
	return render_template(game.type.viz, game=game, inprogress=True, ai0=game.ais[0], ai1=game.ais[1], stream=stream)

@anonymous_blueprint.route("/settings")
def settings():
	print(request.cookies)
	if "gametype" in request.cookies:
		current_gametype = GameType.query.filter(GameType.name.ilike(request.cookies["gametype"])).first()
	return render_template("settings.html", current_gametype=current_gametype,
		lastest_gametype=GameType.lastest(), gametypes=GameType.query.all())
