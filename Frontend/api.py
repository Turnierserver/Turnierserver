from flask import Blueprint, Response, request, abort
from flask.ext.login import current_user, login_user, logout_user, LoginManager, UserMixin
from functools import wraps
import json

from database import AI, User, Game, db
from commons import authenticated, cache
from _cfg import env
from activityfeed import Activity




def json_out(f):
	@wraps(f)
	def wrapper(*args, **kwargs):
		def jsonify_wrap(obj):
			return Response(json.dumps(obj), mimetype='application/json')

		result = f(*args, **kwargs)
		if isinstance(result, tuple):
			# (resp, status_code)
			return jsonify_wrap(result[0]), result[1]
		if isinstance(result, dict):
			return jsonify_wrap(result)
		if isinstance(result, list):
			return jsonify_wrap(result)

		# isnt tuple, dict or list -> must be a Response
		return result

	return wrapper


login_manager = LoginManager()


@login_manager.user_loader
def load_user(username):
	return User.query.filter(User.name == username).first()




class CommonErrors:
	INVALID_ID = ({'error': 'Invalid id.'}, 404)
	NO_ACCESS = ({'error': 'Insufficient permissions.'}, 401)
	IM_A_TEAPOT = ({'error': 'I\'m a teapot.'}, 418)
	NOT_IMPLEMENTED = ({'error': 'Not implemented.'}, 501)


api = Blueprint("api", __name__, url_prefix="/api")

@api.route("/", methods=["GET"])
def api_index():
	return "Ein API Skelett, damit anderes Zeugs implementiert werden kann."

@api.route("/ais", methods=["GET"])
@json_out
def api_ais():
	return [ai.info() for ai in AI.query.all()]

@api.route("/ai/<int:id>", methods=["GET"])
@json_out
def api_ai(id):
	ai = AI.query.get(id)
	if ai:
		return ai.info()
	else:
		return CommonErrors.INVALID_ID

@api.route("/ai/<int:id>/games", methods=["GET"])
@json_out
def api_ai_games(id):
	ai = AI.query.get(id)
	if ai:
		return [assoc.game.info() for assoc in ai.game_assocs]
	else:
		return CommonErrors.INVALID_ID

@api.route("/games")
@json_out
def api_games():
	return [game.info() for game in Game.query.all()]

@api.route("/game/<int:id>", methods=["GET"])
@json_out
def api_game(id):
	game = Game.query.get(id)
	if game:
		return game.info()
	else:
		return CommonErrors.INVALID_ID

@api.route("/users", methods=["GET"])
@json_out
def api_users():
	return [user.info() for user in User.query.all()]

@api.route("/user/<int:id>", methods=["GET"])
@json_out
def api_user(id):
	user = User.query.get(id)
	if user:
		return user.info()
	else:
		return CommonErrors.INVALID_ID

@api.route("/user/<int:id>/icon", methods=["GET"])
@cache.memoize(timeout=env.cache_max_age)
def api_user_icon(id):
	user = User.query.get(id)
	if user:
		return user.icon()
	else:
		abort(404)


@api.route("/login", methods=['POST'])
@json_out
def api_login():
	username = request.form['username']
	password = request.form['password']
	if not username or not password:
		return { 'error': 'Missing username or password' }, 400

	user = User.query.filter(User.name.ilike(username)).first()

	if not user:
		return { 'error': 'Invalid Username.' }, 404

	##Check for PW here :P
	if not True:
		return CommonErrors.NO_ACCESS

	login_user(user)

	Activity(user.name + " hat sich erfolgreich eingeloggt.")

	return { 'error': False }

@api.route("/logout", methods=["GET", "POST"])
@json_out
def api_logout():
	if not current_user.is_authenticated():
		return {'error': 'Not logged in.'}
	logout_user()
	return { 'error': False }

@api.route("/loggedin", methods=['GET'])
@json_out
@authenticated
def api_logged_in():
	return current_user.info()


@api.route("/ai/create")
@json_out
@authenticated
def api_ai_create():
	name = request.args.get('name', 'unbenannte ki')
	desc = request.args.get('desc', 'unbeschriebene ki')
	lang = Lang.query.get(request.args.get('lang', -1))
	if not lang:
		return {'error', 'Invalid Language'}, 404
	ai = AI(name=name, user=current_user, desc=desc, lang=lang)
	db.session.add(ai)
	# es muss zur Datenbank geschrieben werden, um die ID zu bekommen
	db.session.commit()
	return {'error': False, 'ai': ai.info()}

@api.route("/ai/<int:id>/icon", methods=["GET"])
def api_ai_icon(id):
	ai = AI.query.get(id)
	if ai:
		return ai.icon()
	else:
		abort(404)

@api.route("/ai/<int:id>/code")
@json_out
@authenticated
def api_ai_code(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID
	if not ai in current_user.ai_list:
		return CommonErrors.NO_ACCESS

	return CommonErrors.NOT_IMPLEMENTED


@api.route("/user/update")
@json_out
@authenticated
def api_user_update():
	u = current_user
	u.name = request.args.get('name', u.name)
	return u.info()


@api.route("/ai/<int:id>/update")
@json_out
@authenticated
def api_ai_update(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID

	if not ai in current_user.ai_list:
		return CommonErrors.NO_ACCESS

	ai.name = request.args.get('name', ai.name)
	ai.desc = request.args.get('description', ai.desc)

	return ai.info()


@api.route("/ai/<int:id>/submitCode")
@json_out
def not_implemented(*args, **kwargs):
	return CommonErrors.NOT_IMPLEMENTED




#github-bequemlichkeit
@api.route("/gh-webhook", methods=["POST"])
@json_out
def gh_webhook(*args, **kwargs):
	print("gh-webhook triggered")
	print(*args, **kwargs)
	func = request.environ.get('werkzeug.server.shutdown')
	if func is None:
		raise RuntimeError('Not running with the Werkzeug Server')
	func()
	return {"error": False}, 200



