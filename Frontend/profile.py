from flask import Blueprint, render_template, abort, redirect, url_for
from flask.ext.login import current_user
from database import AI, User, Game, Lang, GameType, db
from commons import authenticated_web
from activityfeed import activity_feed
from errorhandling import error

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
	ai = AI(user=current_user, name="Unbenannte KI",
			desc="Unbeschriebene KI", lang=Lang.query.first(),
			type=GameType.query.first())
	db.session.commit()
	return redirect(url_for("profile.edit_ai", id=ai.id))

@profile.route("/ai/<int:id>/edit")
@authenticated_web
def edit_ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.can_access(ai):
		abort(401)
	return render_template("edit_ai.html", ai=ai, langs=Lang.query.all())


@profile.route("/ais/challenge")
@authenticated_web
def ais_challenge():
	own = current_user.ai_list.order_by(AI.last_modified.desc())
	ownfirst = own.first()
	if not ownfirst:
		return error(403, body="Du hast nicht genug eigene KIs.")
	all = AI.query.order_by(AI.id).all()
	allfirst = AI.query.order_by(AI.id).first()
	if not allfirst:
		return error(403, body="Es gibt noch nicht genug AIs!")
	return render_template("challenge.html", own=own, all=all, ownfirst=ownfirst, allfirst=allfirst)


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