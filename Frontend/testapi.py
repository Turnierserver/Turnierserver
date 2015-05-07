from flask import *
from flask.ext.login import current_user, login_user, logout_user, LoginManager, UserMixin
Flask.secret_key = "foobar"

from functools import wraps

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



app = Flask(__name__)
app.debug = True

mock_ai = {
	'name': 'Meine KI',
	'author': 'Ich',
	'version': 1,
	'rating': 0.2
}

mock_game = {
	'ais': [mock_ai]*2,
	'outcome': [1, 2],
	'ranked': False
}

mock_user = {
	'ais': [mock_ai]*2,
	'ranking': 4,
	'description': 'Hallo, ich bin ein Nutzer',
	'username': 'user'
}


login_manager = LoginManager()
login_manager.init_app(app)

class User(UserMixin):
	def __init__(self, d):
		self.d = d
		self.id = d['username']

userlist = [User(mock_user)]
users = {u.id: u for u in userlist}

@login_manager.user_loader
def load_user(userid):
    return users.get(userid, None)



class CommonErrors:
	INVALID_ID = ({'error': 'Invalid id.'}, 404)
	NO_ACCESS = ({'error': 'Insufficient permissions.'}, 401)


@app.route("/api/", methods=["GET"])
def api_index():
	return "Ein API Skelett, damit anderes Zeugs implementiert werden kann."

@app.route("/api/ais", methods=["GET"])
@json_out
def api_ais():
	return [mock_ai]*3

@app.route("/api/ai/<int:id>", methods=["GET"])
@json_out
def api_ai(id):
	return {0: mock_ai}.get(id, CommonErrors.INVALID_ID)

@app.route("/api/games")
@json_out
def api_games():
	return [mock_game]*3

@app.route("/api/game/<int:id>", methods=["GET"])
@json_out
def api_game(id):
	return {0: mock_game}.get(id, CommonErrors.INVALID_ID)

@app.route("/api/users", methods=["GET"])
@json_out
def api_users():
	return [mock_user]*3

@app.route("/api/user/<int:id>", methods=["GET"])
@json_out
def api_user(id):
	return {0: mock_user}.get(id, CommonErrors.INVALID_ID)


@app.route("/api/login", methods=['POST'])
@json_out
def api_login():
	username = request.form['username']
	password = request.form['password']
	if not username or not password:
		return { 'error': 'Missing username or password' }, 400

	if not username in users:
		return { 'error': 'Invalid Username.' }, 404

	##Check for PW here :P
	if not True:
		return CommonErrors.NO_ACCESS

	login_user(users[username])

	return { 'error': False }

@app.route("/api/logout", methods=["GET", "POST"])
@json_out
def api_logout():
	logout_user()
	return { 'error': False }

@app.route("/api/loggedin", methods=['GET'])
@json_out
def api_logged_in():
	if current_user:
		if not current_user.is_anonymous:
			return current_user.d
	return CommonErrors.NO_ACCESS


app.run(host="0.0.0.0", port=5000)