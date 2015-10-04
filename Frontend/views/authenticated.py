from flask import Blueprint, render_template, abort, redirect, url_for
from flask.ext.login import current_user
from database import AI, User, Game, Lang, GameType, Tournament, UserTournamentAi, db, ftp
from commons import authenticated_web
from logger import logger
from errorhandling import error
from unittest import mock

authenticated_blueprint = Blueprint("authenticated", __name__)

@authenticated_blueprint.route("/profile")
@authenticated_web
def current_profile():
	user = current_user
	ais = AI.filtered().filter(AI.user == user).all()
	columns = [ais[i:i+3] for i in range(0, len(ais), 3)]
	return render_template("profile.html", columns=columns, user=user, gametype=GameType.selected())

@authenticated_blueprint.route("/profile/<int:id>")
@authenticated_web
def profile_id(id):
	user = User.query.get(id)
	if not user:
		abort(404)
	if not current_user.can_access(user):
		abort(403)

	ais = AI.filtered().filter(AI.user == user).all()
	columns = [ais[i:i+3] for i in range(0, len(ais), 3)]
	return render_template("profile.html", columns=columns, user=user, gametype=GameType.selected())



@authenticated_blueprint.route("/create_ai")
@authenticated_web
def create_ai():
	ai = AI(user=current_user, name="Unbenannte KI",
			desc="Unbeschriebene KI", lang=Lang.query.first(),
			type=GameType.selected())
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
	t = UserTournamentAi.query.filter(UserTournamentAi.user == current_user)\
	    .filter(UserTournamentAi.type == ai.type).first() is None
	t = t and ai.active_version()
	return render_template("edit_ai.html", ai=ai, langs=Lang.query.all(), can_enter_tournament=t)

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

	ftp_url = "AIs/{}/v{}/{}".format(id, ai.latest_version().version_id, path)

	root_url = url_for("authenticated.file_browser", id=id, path="")
	path_url = [(ai.name, url_for("authenticated.edit_ai", id=id)), ("(root)", root_url)]
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
				code = remote_obj.read()
				return render_template("ai_file_editor.html", path=path_url,
										code=code, ai=ai, is_libs=submit_name == "libraries.txt",
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
		logger.warning("err")
		return render_template("ai_file_browser.html", connected=False, ai=ai)


@authenticated_blueprint.route("/ai/<int:id>/qualify")
@authenticated_web
def qualify_ai(id):
	ai = AI.query.get(id)
	if not ai:
		abort(404)
	if not current_user.can_access(ai):
		abort(401)

	#quali_ai = AI.query.get(-ai.type.id)
	quali_ai = mock.Mock()
	quali_ai.id = -ai.type.id
	version = quali_ai.active_version()
	version.compiled, version.qualified, version.frozen = True, True, True
	version.version_id = 1
	quali_ai.name = "QualiKI"
	quali_ai.user = User.query.filter(User.admin == True).first()
	quali_ai.lang = Lang.query.first()

	stream = url_for("api.ai_qualify", id=id)

	return render_template(ai.type.viz, qualify=True, ai0=ai, ai1=quali_ai, inprogress=True, stream=stream)


@authenticated_blueprint.route("/ais/challenge")
@authenticated_web
def challenge(other=None):
	own_ais = AI.filtered().filter(AI.user == current_user).order_by(AI.last_modified.desc()).all()

	if len(own_ais) < 1:
		return error(403, body="Du hast nicht genug eigene KIs.")

	own_ais = [ai for ai in own_ais if ai.latest_frozen_version()]
	# TODO: ki workflow verbessern

	if len(own_ais) < 1:
		return error(403, body="Du hast keine KIs deren letzte Version freigegeben ist.")

	all_ais = AI.filtered().order_by(AI.id).all()
	if len(all_ais) < 2:
		return error(403, body="Es gibt noch nicht genug KIs.")

	all_ais = [ai for ai in all_ais if ai.latest_frozen_version()]
	if len(all_ais) < 2:
		return error(403, body="Es gibt nicht genug freigegebene KIs.")
	#roles = ["Rolle"+str(i) for i, r in enumerate(gametype.roles)]
	return render_template("challenge.html", own=own_ais, all=all_ais, ownfirst=own_ais[0], allfirst=all_ais[0])


@authenticated_blueprint.route("/admin")
@authenticated_web
def admin():
	if not current_user.admin:
		abort(401)
	return render_template("admin.html", tournaments=Tournament.query.filter(Tournament.executed == False).all())
