from flask import render_template
from flask.ext.login import current_user

errors = {
	401: ('unauthorized', 'Du hast nicht genug Rechte, um diese Aktion auszufueren.'),
	403: ('forbidden', 'Zugang verboten.'),
	404: ('not found', 'Hier ist nichts zu sehen.'),
	500: ('internal server error', 'Es gab einen Serverfehler.'),
	501: ('not implemented', 'Dieses Feature wurde noch nicht implementiert.')
}


def error(errorcode, body=None, title=None):
	if errorcode in errors:
		title = title or str(errorcode) + ": " + errors[errorcode][0].upper()
		body = body or errors[errorcode][1]
	else:
		title = title or str(errorcode)
		body = body or "Dieser Statuscode wurde noch nicht dokumentiert."
	return render_template("error.html", title=title, body=body), errorcode

def handle_errors(app):
	#for code in errors:
	#	app.errorhandler(code)(lambda e, c=code: error(c))
	@app.errorhandler(404)
	def not_found(e):
		return error(404)
