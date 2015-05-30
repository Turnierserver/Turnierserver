from flask import Blueprint, Response, request, abort
from flask.ext.login import current_user, login_user, logout_user, LoginManager, UserMixin
from functools import wraps
from queue import Empty
from werkzeug.utils import secure_filename
from sqlalchemy.orm.exc import NoResultFound
import json
import magic

from database import AI, User, Game, Lang, db, populate, ftp
from backend import backend
from commons import authenticated, cache, CommonErrors, bcrypt
from _cfg import env
from activityfeed import Activity
from sse import sse_stream





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


@api.route("/game/<int:id>/log")
@sse_stream
def api_game_log(id):
	game = Game.query.get(id)
	if game:
		for chunk in []:
			yield chunk
	else:
		return CommonErrors.INVALID_ID

@api.route("/game/inprogress/<int:id>/log")
@sse_stream
def api_game_inprogress_log(id):
	game = None
	if game:
		for chunk in []:
			yield chunk
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
	if request.mimetype == "application/json":
		if not request.json:
			return {"error": "Not valid JSON."}, 400
		username = request.json.get("username", None)
		password = request.json.get("password", None)
		remember = request.json.get("remember", False)
	elif request.mimetype == "application/x-www-form-urlencoded":
		username = request.form['username']
		password = request.form['password']
		remember = request.form.get("remember", False)
		if remember:
			remember = True
	else:
		return {"error": "Wrong Content-Type, must be application/json or application/x-www-form-urlencoded"}, 400
	if not username or not password:
		return { 'error': 'Missing username or password' }, 400

	## Auch EMails akzeptieren?
	user = User.query.filter(User.name.ilike(username)).first()

	if not user:
		return { 'error': 'Invalid Username.' }, 404

	if not user.check_pw(password):
		return {'error': 'Wrong password.'}, 400

	login_user(user, remember=remember)

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

@api.route("/user/create", methods=['GET', 'POST'])
@json_out
def api_user_create():
	if request.mimetype == "application/json":
		if not request.json:
			return {"error": "Not valid JSON."}, 400
		username = request.json.get("username", None)
		lastname = request.json.get("lastname", None)
		firstname = request.json.get("firstname", None)
		password = request.json.get("password", None)
		email = request.json.get("email", None)
	elif request.mimetype == "application/x-www-form-urlencoded":
		username = request.form['username']
		lastname = request.form['lastname']
		firstname = request.form['firstname']
		password = request.form['password']
		email = request.form.get("email", None)
	else:
		return {"error": "Wrong Content-Type, must be application/json or application/x-www-form-urlencoded"}, 400
	if not username or not password or not email:
		return { 'error': 'Missing username, password or email' }, 400

	try:
		User.query.filter(User.name.ilike(username)).one()
		return {'error': 'Username already registered'}, 400
	except NoResultFound:
		pass

	try:
		User.query.filter(User.email.ilike(email)).one()
		return {'error': 'EMail already registered'}, 400
	except NoResultFound:
		pass

	user = User(name=username, firstname=firstname,
				lastname=lastname, email=email)
	user.set_pw(password)

	db.session.add(user)
	# es muss zur Datenbank geschrieben werden, um die Infos zu bekommen
	db.session.commit()

	login_user(user)

	return {'error': False, 'user': user.info()}, 200


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

def upload_single_file(request, path, image=False):
	print(request.mimetype)
	print(request.files)
	print(request.data)
	if request.mimetype == "multipart/form-data":
		if len(request.files) != 1:
			return {"error": "Invalid number of files attached."}, 400
		content = list(request.files.values())[0].read()
	else:
		content = request.data

	if image:
		mime = magic.from_buffer(content, mime=True).decode("ascii")
		print(mime, magic.from_buffer(content))
		if not "image/" in mime:
			return {"error": "Invalid mimetype for an image.", "mimetype": mime}

	@ftp.safe
	def f():
		with ftp.ftp_host.open(path, "wb") as f:
			f.write(content)
		return {"error": False}, 200
	try:
		return f()
	except ftp.err:
		return CommonErrors.FTP_ERROR

@api.route("/ai/<int:id>/upload_icon", methods=["POST"])
@json_out
@authenticated
def api_ai_upload_icon(id):
	ai = AI.query.get(id)
	if ai:
		cache.delete_memoized(api_ai_icon, id)
		return upload_single_file(request, "AIs/"+str(id)+"/icon.png", image=True)
	else:
		return CommonErrors.INVALID_ID

@api.route("/ai/<int:id>/reset_icon", methods=["POST"])
@json_out
@authenticated
def api_ai_reset_icon(id):
	ai = AI.query.get(id)
	if ai:
		cache.delete_memoized(api_ai_icon, id)
		@ftp.safe
		def f():
			path = "AIs/"+str(id)+"/icon.png"
			if not ftp.ftp_host.path.isfile(path):
				return {"error": "No custom Icon"}, 400
			ftp.ftp_host.remove(path)
			return {"error": False}, 200

		try:
			return f()
		except ftp.err:
			return CommonErrors.FTP_ERROR
	else:
		return CommonErrors.INVALID_ID

@api.route("/user/<int:id>/upload_icon", methods=["POST"])
@json_out
@authenticated
def api_user_upload_icon(id):
	user = User.query.get(id)
	if user:
		cache.delete_memoized(api_user_icon, id)
		return upload_single_file(request, "Users/"+str(id)+"/icon.png", image=True)
	else:
		return CommonErrors.INVALID_ID

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

@api.route("/ai/<int:id>/copy_example_code", methods=["GET", "POST"])
@json_out
@authenticated
def api_ai_copy_example_code(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID
	if not current_user.can_access(ai):
		return CommonErrors.NO_ACCESS

	try:
		ai.ftp_sync()
	except ftp.err:
		return CommonErrors.FTP_ERROR
	if not ai.copy_example_code():
		return CommonErrors.FTP_ERROR

	return ({"error": False}, 200)


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


@api.route("/ai/<int:id>/compile", methods=["GET"])
@authenticated
@sse_stream
def api_ai_compile(id):
	ai = AI.query.get(id)
	if not ai:
		return (CommonErrors.INVALID_ID[0]["error"], "error")
	if not current_user.can_access(ai):
		return (CommonErrors.NO_ACCESS[0]["error"], "error")
	reqid = backend.request_compile(ai)
	yield ("compiling", "status")
	yield ("F: Kompilierung mit ID {} angefangen.\n".format(reqid), "set_text")



	@ftp.safe
	def f():
		yield ("F: Kompilierungs-Log:\n", "log")
		path = "AIs/{}/bin/v{}-compile.out".format(ai.id, ai.lastest_version().version_id)
		if ftp.ftp_host.path.isfile(path):
			with ftp.ftp_host.open(path, "r", encoding="utf-8") as f:
				yield (path+"\n"+f.read(), "log")
		else:
			yield (path + " existiert noch nicht.\n", "log")


	timed_out = 0
	while True:
		resp = backend.lock_for_req(reqid, timeout=5)
		b_req = backend.request(reqid)
		if not resp:
			#yield ("F: backend timeout\n", "log")
			yield (".", "log")
			timed_out += 1
			if timed_out > 20:
				yield ("\nDas Backend sendet nichts.", "log")
				yield ("\nVersuch es nochmal.", "log")
				return
		else:
			if timed_out > 0:
				yield ("\n", "log")
			timed_out = 0
			yield ("B: " + str(resp) + "\n", "log")
			if "success" in b_req:
				if b_req["success"]:
					yield ("Anfrage erfolgreich beendet\n", "log")
				else:
					yield ("Anfrage beendet\n", "log")
					if "exception" in b_req:
						yield (b_req["exception"], "log")
				try:
					yield from f()
				except ftp.err:
					yield ("FTP-Error\n", "log")
				return
			elif "status" in resp:
				if resp["status"] == "processed":
					yield ("Anfrage angefangen\n", "log")



@api.route("/ai/<int:id>/upload", methods=["POST"])
@json_out
@authenticated
def ai_upload(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID
	if not current_user.can_access(ai):
		return CommonErrors.NO_ACCESS

	if not ('path' in request.form and 'filename' in request.form and 'data' in request.form):
		return CommonErrors.BAD_REQUEST

	path = request.form['path']
	if path.startswith("/") or ".." in path:
		return CommonErrors.BAD_REQUEST
	if not path.endswith("/"):
		path += "/"
	path = "AIs/{}/v{}/{}".format(id, ai.lastest_version().version_id, path)
	filename = secure_filename(request.form['filename'])
	if not len(filename):
		return ({"error": "Missing filename."}, 400)
	data = request.form["data"]

	@ftp.safe
	def f():
		if not ftp.ftp_host.path.isdir(path):
			return ({'error': 'Invalid path.'}, 400)

		with open("tmp", "w") as f:
			f.write(data)

		ftp.ftp_host.upload("tmp", path + filename)
		return ({"error": False}, 200)

	try:
		return f()
	except ftp.err:
		return CommonErrors.FTP_ERROR



@api.route("/ai/<int:id>/delete_file", methods=["POST"])
@json_out
@authenticated
def ai_delete_file(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID
	if not current_user.can_access(ai):
		return CommonErrors.NO_ACCESS

	if not ('path' in request.form and 'filename' in request.form):
		return CommonErrors.BAD_REQUEST

	path = request.form['path']
	if path.startswith("/") or ".." in path:
		return CommonErrors.BAD_REQUEST
	if not path.endswith("/"):
		path += "/"
	path = "AIs/{}/v{}/{}".format(id, ai.lastest_version().version_id, path)
	filename = secure_filename(request.form['filename'])

	@ftp.safe
	def f():
		if not ftp.ftp_host.path.isdir(path):
			return ({'error': 'Invalid path.'}, 400)

		if ftp.ftp_host.path.isfile(path+filename):
			ftp.ftp_host.remove(path + filename)
		else:
			ftp.ftp_host.rmtree(path+filename)
		return ({"error": False}, 200)

	try:
		return f()
	except ftp.err:
		return CommonErrors.FTP_ERROR

@api.route("/ai/<int:id>/create_folder", methods=["POST"])
@json_out
@authenticated
def ai_create_folder(id):
	ai = AI.query.get(id)
	if not ai:
		return CommonErrors.INVALID_ID
	if not current_user.can_access(ai):
		return CommonErrors.NO_ACCESS

	if not ('path' in request.form and 'name' in request.form):
		return CommonErrors.BAD_REQUEST

	path = request.form['path']
	if path.startswith("/") or ".." in path:
		return CommonErrors.BAD_REQUEST
	if not path.endswith("/"):
		path += "/"
	path = "AIs/{}/v{}/{}".format(id, ai.lastest_version().version_id, path)
	name = secure_filename(request.form['name'])
	if not len(name):
		return ({"error": "Missing name."}, 400)

	@ftp.safe
	def f():
		if not ftp.ftp_host.path.isdir(path):
			return ({'error': 'Invalid path.'}, 400)

		ftp.ftp_host.mkdir(path + name)
		return ({"error": False}, 200)

	try:
		return f()
	except ftp.err:
		return CommonErrors.FTP_ERROR


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
		try:
			ai.ftp_sync()
		except ftp.err:
			print("failed to Sync " + ai.name)
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

@api.route("/game_list_sse", methods=["GET"])
@sse_stream
def game_list_sse():
	yield ("connected", "connected")
	q = backend.subscribe_game_update()
	while True:
		try:
			update = q.get(timeout=5)
			if "status" in update:
				if update["status"] == "processed":
					yield ("", "new_game")
		except Empty:
			# falls es keine Verbindung mehr gibt wird der Generator hier beendet.
			yield None
	yield ("", "new_game")
	yield ("""{"id": 1, "status": "1/42"}""", "update")


@api.route("/upload_game_libs/<int:id>/<string:lang>", methods=["POST"])
@json_out
@admin_required
def upload_game_libs(id, lang):
	## id und lang gegen eigene Datenbank prüfen?

	@ftp.safe
	def f():
		if not ftp.ftp_host.path.isdir("Games/{}/{}".format(id, lang)):
			return {"error": "Invalid GameID or Lang"}, 400
		if not "X-FileName" in request.headers:
			return {"error": "Missing filename"}, 400
		filename = request.headers.get("X-FileName")
		with ftp.ftp_host.open("Games/{}/{}/{}".format(id, lang, filename), "wb") as f:
			f.write(request.data)
		return {"error": False}, 200

	try:
		return f()
	except ftp.err:
		return CommonErrors.FTP_ERROR

@api.route("/upload_game_logic/<int:id>", methods=["POST"])
@json_out
@admin_required
def upload_game_logic(id):
	return upload_single_file(request, "Games/"+secure_filename(str(id))+"/Logic.jar")


@api.route("/simple_players/<int:id>")
def simple_players(id):
	"""
	in der ESU:
	SimplePlayer/Java/
				/Python/
	im FTP:
	Games/1/Java/example_ai
			Python/example_ai
	"""
	@ftp.failsafe_locked
	def f(self):
		if ftp.ftp_host.path.isfile("Games/"+secure_filename(str(id))+"/simple_players.zip"):
			return ftp.send_file("Games/"+secure_filename(str(id))+"/simple_players.zip")
		else:
			abort(404)
	return f()
