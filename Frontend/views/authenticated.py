from flask import Blueprint, render_template, abort, redirect, url_for
from flask.ext.login import current_user
from database import AI, User, Game, Lang, GameType, db, ftp
from commons import authenticated_web
from activityfeed import activity_feed
from errorhandling import error

authenticated_blueprint = Blueprint("authenticated", __name__)

@authenticated_blueprint.route("/profile")
@authenticated_web
def current_profile():
	user = current_user
	ais = AI.query.filter(AI.user == user).filter(AI.id >= 0).all()
	columns = [ais[i:i+3] for i in range(0, len(ais), 3)]
	return render_template("profile.html", columns=columns, user=user)

@authenticated_blueprint.route("/profile/<int:id>")
@authenticated_web
def profile_id(id):
	user = User.query.get(id)
	if not user:
		abort(404)
	if not current_user.can_access(user):
		abort(403)

	ais = AI.query.filter(AI.user == user).filter(AI.id >= 0).all()
	columns = [ais[i:i+3] for i in range(0, len(ais), 3)]
	return render_template("profile.html", columns=columns, user=user)



@authenticated_blueprint.route("/create_ai")
@authenticated_web
def create_ai():
	ai = AI(user=current_user, name="Unbenannte KI",
			desc="Unbeschriebene KI", lang=Lang.query.first(),
			type=GameType.query.first())
	## current gametype
	db.session.commit()
	return redirect(url_for("authenticated.edit_ai", id=ai.id))

@authenticated_blueprint.route("/ai/<int:id>/edit")
@authenticated_web
def edit_ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.can_access(ai):
		abort(401)
	return render_template("edit_ai.html", ai=ai, langs=Lang.query.all())

@authenticated_blueprint.route("/ai/<int:id>/compile")
@authenticated_web
def compile_ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.can_access(ai):
		abort(401)
	return render_template("compile_ai.html", ai=ai)

@authenticated_blueprint.route("/ai/<int:id>/files/<path:path>")
@authenticated_blueprint.route("/ai/<int:id>/files/")
@authenticated_blueprint.route("/ai/<int:id>/files")
@authenticated_web
def file_browser(id, path=""):

	if '..' in path or path.startswith('/'):
		abort(404)

	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.can_access(ai):
		abort(401)

	## cleanup?

	curr_url = url_for("authenticated.file_browser", id=id, path=path)
	if not curr_url.endswith("/"):
		curr_url += "/"

	ftp_url = "AIs/{}/v{}/{}".format(id, ai.lastest_version().version_id, path)

	root_url = url_for("authenticated.file_browser", id=id, path="")
	path_url = [(ai.name, root_url)]
	url = root_url[:-1]
	for p in path.split("/"):
		url += "/" + p
		path_url.append((p, url))


	@ftp.safe
	def f():
		if ftp.ftp_host.path.isfile(ftp_url):
			submit_path = "/".join(path.split("/")[:-1])
			submit_name = path.split("/")[-1]
			with ftp.ftp_host.open(ftp_url, "r") as remote_obj:
				return render_template("ai_file_editor.html", path=path_url,
										code=remote_obj.read(), ai=ai,
										submit_name=submit_name, submit_path=submit_path)

		objs = []
		files = ftp.ftp_host.listdir(ftp_url)
		for f in files:
			tmps = int(ftp.ftp_host.path.getmtime(ftp_url + "/" + f))
			objs.append(dict(
				name=f,
				isfile=ftp.ftp_host.path.isfile(ftp_url+"/"+f),
				url=curr_url + f,
				timestamp=tmps
			))
		return render_template("ai_file_browser.html", ai=ai, objs=objs,
								path=path_url, connected=True, submit_path=path)

	try:
		return f()
	except ftp.err:
		print("err")
		return render_template("ai_file_browser.html", connected=False, ai=ai)


@authenticated_blueprint.route("/ai/<int:id>/qualify")
@authenticated_web
def qualify_ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.can_access(ai):
		abort(401)

	quali_ai = AI.query.get(-ai.type.id)


	stream = url_for("api.ai_qualify", id=id)

	return render_template(ai.type.viz, qualify=True, ai0=ai, ai1=quali_ai, inprogress=True, stream=stream)


@authenticated_blueprint.route("/ais/challenge")
@authenticated_web
def ais_challenge():
	gametype = GameType.query.first()
	## current gametype

	# own = current_user.ai_list.filter(AI.type==gametype).order_by(AI.last_modified.desc()).all()
	# aus irgend nem komischen grund funkioniert order_by bei der ai_list von current_user nicht
	own = AI.query.filter(AI.user == current_user).filter(AI.type == gametype).filter(AI.id >= 0).order_by(AI.last_modified.desc()).all()
	if len(own) < 1:
		return error(403, body="Du hast nicht genug eigene KIs.")
	all = AI.query.filter(AI.id >= 0).order_by(AI.id).all()
	if len(all) < 1:
		return error(403, body="Es gibt noch nicht genug AIs!")
	#roles = ["Rolle"+str(i) for i, r in enumerate(gametype.roles)]
	return render_template("challenge.html", own=own, all=all, ownfirst=own[0], allfirst=all[0])


@authenticated_blueprint.route("/activityfeed")
@authenticated_web
def activityfeed():
	if not current_user.admin:
		abort(401)
	return render_template("activityfeed.html", activities=activity_feed.feed[::-1])

@authenticated_blueprint.route("/admin")
@authenticated_web
def admin():
	if not current_user.admin:
		abort(401)
	return render_template("admin.html")
