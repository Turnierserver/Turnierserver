####################################################################################
# errorhandling.py
#
# Copyright (C) 2015 Pixelgaffer
#
# This work is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as published by the
# Free Software Foundation; either version 2 of the License, or any later
# version.
#
# This work is distributed in the hope that it will be useful, but without
# any warranty; without even the implied warranty of merchantability or
# fitness for a particular purpose. See version 2 and version 3 of the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
####################################################################################
from flask import render_template

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
	for code in errors:
		app.errorhandler(code)(lambda e, c=code: error(c))
	#@app.errorhandler(404)
	#def not_found(e):
	#	return error(404)
