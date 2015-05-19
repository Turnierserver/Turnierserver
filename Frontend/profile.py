from flask import Blueprint, render_template, abort
from flask.ext.login import current_user
from database import AI, User, Game, Lang
from commons import authenticated_web

profile = Blueprint("profile", __name__)
@profile.route("/profile")
@authenticated_web
def current_profile():
	ais = AI.query.filter(AI.user == current_user).all()
	columns = [ais[i:i+3] for i in range(0, len(ais), 3)]
	return render_template("profile.html", columns=columns)

@profile.route("/create_ai")
@authenticated_web
def create_ai():
	return render_template("create_ai.html")

@profile.route("/ai/<int:id>/edit")
@authenticated_web
def edit_ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.can_access(ai):
		abort(401)
	return render_template("edit_ai.html", ai=ai, langs=Lang.query.all())

@profile.route("/activityfeed")
@authenticated_web
def activityfeed():
	if not current_user.admin: abort(401)
	return render_template("activityfeed.html", activities=activity_feed.feed[::-1])

@profile.route("/admin")
@authenticated_web
def admin():
	if not current_user.admin: abort(401)
	return render_template("admin.html")