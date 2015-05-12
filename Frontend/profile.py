from flask import Blueprint, render_template, abort
from flask.ext.login import current_user
from database import AI, User, Game

profile = Blueprint("profile", __name__)
@profile.route("/profile")
def current_profile():
	return render_template("profile.html")
