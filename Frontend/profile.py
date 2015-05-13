from flask import Blueprint, render_template, abort
from flask.ext.login import current_user
from database import AI, User, Game, Lang

profile = Blueprint("profile", __name__)
@profile.route("/profile")
def current_profile():
	return render_template("profile.html")

@profile.route("/create_ai")
def create_ai():
	return render_template("create_ai.html")

@profile.route("/ai/<int:id>/edit")
def edit_ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.is_authenticated():
		abort(401)
	if not ai.user == current_user:
		abort(401)
	return render_template("edit_ai.html", ai=ai, langs=Lang.query.all())

