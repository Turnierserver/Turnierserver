from flask import Blueprint, render_template, abort, flash, url_for, request, Markup, send_file, make_response, current_app
from flask.ext.login import current_user
from datetime import timedelta
from functools import update_wrapper
from database import AI, User, Game, GameType, Game_inprogress
from backend import backend
from logger import logger
import json
import markdown
import codecs

anonymous_blueprint = Blueprint("anonymous", __name__)

@anonymous_blueprint.route("/")
def index():
	with codecs.open("../aktuelles.md", "r", encoding='utf-8') as f:
		aktuelles = f.read()
	aktuelles = Markup(markdown.markdown(aktuelles))
	return render_template("index.html", aktuelles=aktuelles)

@anonymous_blueprint.route("/ai_list")
def ai_list():
	ais = AI.filtered().all()
	ais = [ai for ai in ais if ai.active_version()]
	if len(ais) == 0:
		flash("Es gibt keine KIs des aktuellen Spieltypen!")
		abort(403)
	if current_user and current_user.is_authenticated:
		data = [ai for ai in AI.filtered() if ai.user == current_user and ai.active_version()]
		if len(data):
			return render_template("ai_list.html", ais=ais, type=ais[0].type,
		                           own=data, own_user_id=current_user.id)
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

@anonymous_blueprint.route("/game/<int:id>/mini")
def game_mini(id):
	game = Game.query.get(id)
	if not game:
		abort(404)
	stream = url_for("api.game_log", id=id)
	return render_template(game.type.viz, game=game, inprogress=False, ai0=game.ais[0], ai1=game.ais[1], stream=stream, mini=True)


@anonymous_blueprint.route("/game/inprogress/<int:id>")
def inprogress_game(id):
	## inpgrogress type
	if not backend.request(id):
		abort(404)
	if not backend.request(id)["action"] == "start":
		logger.warning("Invalid gameid!")
		abort(404)
	game = Game_inprogress(id, backend.request(id))

	stream = url_for("api.game_inprogress_log", id=game.id)
	return render_template(game.type.viz, game=game, inprogress=True, ai0=game.ais[0], ai1=game.ais[1], stream=stream)

@anonymous_blueprint.route("/game/inprogress/<int:id>/mini")
def inprogress_game_mini(id):
	## inpgrogress type
	if not backend.request(id):
		abort(404)
	if not backend.request(id)["action"] == "start":
		logger.warning("Invalid gameid!")
		abort(404)
	game = Game_inprogress(id, backend.request(id))

	stream = url_for("api.game_inprogress_log", id=game.id)
	return render_template(game.type.viz, game=game, inprogress=True, ai0=game.ais[0], ai1=game.ais[1], stream=stream, mini=True)



def crossdomain(origin=None, methods=None, headers=None,
				max_age=21600, attach_to_all=True,
				automatic_options=True):
	if methods is not None:
		methods = ', '.join(sorted(x.upper() for x in methods))
	if headers is not None and not isinstance(headers, (str, bytes)):
		headers = ', '.join(x.upper() for x in headers)
	if not isinstance(origin, (str, bytes)):
		origin = ', '.join(origin)
	if isinstance(max_age, timedelta):
		max_age = max_age.total_seconds()

	def get_methods():
		if methods is not None:
			return methods

		options_resp = current_app.make_default_options_response()
		return options_resp.headers['allow']

	def decorator(f):
		def wrapped_function(*args, **kwargs):
			if automatic_options and request.method == 'OPTIONS':
				resp = current_app.make_default_options_response()
			else:
				resp = make_response(f(*args, **kwargs))
			if not attach_to_all and request.method != 'OPTIONS':
				return resp

			h = resp.headers

			h['Access-Control-Allow-Origin'] = origin
			h['Access-Control-Allow-Methods'] = get_methods()
			h['Access-Control-Max-Age'] = str(max_age)
			if headers is not None:
				h['Access-Control-Allow-Headers'] = headers
			return resp

		f.provide_automatic_options = False
		return update_wrapper(wrapped_function, f)
	return decorator

@anonymous_blueprint.route("/api.yaml")
@crossdomain(origin='*', headers=["Content-Type"])
def api_yaml():
	return send_file("api.yaml")
