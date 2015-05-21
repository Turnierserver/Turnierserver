from flask import Blueprint, Response, request, abort
from flask.ext.login import current_user, login_user, logout_user, LoginManager, UserMixin
from functools import wraps
import json

from database import AI, User, Game, Lang, db, populate
from backend import backend
from commons import authenticated, cache, CommonErrors
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


def admin_required(f):
	@wraps(f)
	def wrapper(*args, **kwargs):
		if current_user:
			if current_user.is_authenticated():
				if current_user.admin:
					return f(*args, **kwargs)
		return CommonErrors.NO_ACCESS
	return wrapper



login_manager = LoginManager()
##
@login_manager.user_loader
def load_user(id):
	return User.query.get(id)


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
@cache.memoize(timeout=env.cache_max_age)
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
	if not current_user.can_access(ai):
		return CommonErrors.NO_ACCESS

	return CommonErrors.NOT_IMPLEMENTED


@api.route("/user/update")
@json_out
@authenticated
def api_user_update():
	u = current_user
	u.name = request.args.get('name', u.name)
	return u.info()


@api.route("/ai/<int:id>/update", methods=["POST"])
@json_out
@authenticated
def api_ai_update(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID

	if not current_user.can_access(ai):
		return CommonErrors.NO_ACCESS

	a = Activity("AI " + ai.name + " geaendert")
	a.extratext = str(ai) + " -> "

	ai.set_name(request.form.get('name', ai.name))
	ai.desc = request.form.get('description', ai.desc)
	if 'lang' in request.form:
		l = Lang.query.get(request.form.get('lang'))
		if l:
			ai.lang = l

	if 'extra[]' in request.form:
		extras = request.form.getlist("extra[]")
		ai.lastest_version().extras(extras)

	# es muss zur Datenbank geschrieben werden, um die aktuellen Infos zu bekommen
	db.session.commit()

	a.extratext += str(ai)

	ai.updated()

	return ai.info()

@api.route("/ai/<int:id>/delete", methods=["GET", "POST"])
@json_out
@authenticated
def api_ai_delete(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID

	if not current_user.can_access(ai):
		return CommonErrors.NO_ACCESS

	Activity("AI " + ai.name + " von " + current_user.name + " geloescht!")
	ai.delete()
	return {"error": False}


@api.route("/ai/<int:id>/submitCode")
@json_out
def not_implemented(*args, **kwargs):
	return CommonErrors.NOT_IMPLEMENTED


@api.route("/games/start", methods=["POST"])
@json_out
@authenticated
def start_game():
	if not 'ai[]' in request.form:
		return CommonErrors.INVALID_ID

	ais = request.form.getlist("ai[]")
	print(ais)
	ais = [AI.query.get(ai) for ai in ais]
	print(ais)
	if not all(ais):
		return CommonErrors.INVALID_ID

	if not any([current_user.can_access(ai) for ai in ais]):
		return CommonErrors.NO_ACCESS

	ai_versions = [(ai, ai.lastest_version()) for ai in ais]
	backend.request_game(ai_versions)

	return {"error": False}




@api.route("/admin/ftp_sync")
@json_out
@admin_required
def admin_ftp_sync():
	Activity(current_user.name + " hat FTP-Sync ausgelöst.")
	for ai in AI.query.all():
		ai.ftp_sync()
	return {"error": False}

@api.route("/admin/clear_db")
@json_out
@admin_required
def admin_clear_db():
	Activity(current_user.name + " hat Datenbanklöschung ausgelöst.")
	db.drop_all()
	populate(5)
	return {"error": False}


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



