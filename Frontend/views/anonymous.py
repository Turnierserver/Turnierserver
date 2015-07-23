from flask import Blueprint, render_template, abort, flash, url_for, request, Markup
from flask.ext.login import current_user
import markdown
from database import AI, User, Game, GameType, Game_inprogress
from activityfeed import Activity
from backend import backend
import json

anonymous_blueprint = Blueprint("anonymous", __name__)

@anonymous_blueprint.route("/")
def index():
	with open("../aktuelles.md", "r") as f:
		aktuelles = f.read()
	aktuelles = Markup(markdown.markdown(aktuelles))
	return render_template("index.html", aktuelles=aktuelles)

@anonymous_blueprint.route("/ai_list")
def ai_list():
	ais = AI.filtered().all()
	if len(ais) == 0:
		flash("Es gibt keine KIs des aktuellen Spieltypen!")
		abort(403)
	return render_template("ai_list.html", ais=ais, type=ais[0].type)

@anonymous_blueprint.route("/ai/<int:id>")
def ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	return render_template("ai.html", ai=ai, version_count=len(ai.version_list), game_count=len(ai.game_assocs))

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
	gametype = GameType.selected()
	query = query.filter(Game.type == gametype)

	return render_template("game_list.html", game_list=query.all(), in_progress_games=backend.inprogress_games(), gametype=gametype)

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
