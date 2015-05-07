from flask import *
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

class CommonErrors:
	INVALID_ID = ({'error': 'Invalid id.'}, 404)
	NO_ACCESS = ({'error': 'Insufficient permissions.'}, 401)


@app.route("/api/", methods=["GET"])
def index():
	return "Ein API Skelett, damit anderes Zeugs implementiert werden kann."

@app.route("/api/ais", methods=["GET"])
@json_out
def ais():
	return [mock_ai]*3

@app.route("/api/ai/<int:id>", methods=["GET"])
@json_out
def ai(id):
	return {0: mock_ai}.get(id, CommonErrors.INVALID_ID)

@app.route("/api/games")
@json_out
def games():
	return [mock_game]*3

@app.route("/api/game/<int:id>", methods=["GET"])
@json_out
def game(id):
	return {0: mock_game}.get(id, CommonErrors.INVALID_ID)

@app.route("/api/users", methods=["GET"])
@json_out
def users():
	return [mock_user]*3

@app.route("/api/user/<int:id>", methods=["GET"])
@json_out
def user(id):
	return {0: mock_user}.get(id, CommonErrors.INVALID_ID)


app.run(host="0.0.0.0", port=5000)