from database import db
from flask import abort, render_template
from flask.ext.login import current_user
from flask.ext.cache import Cache
from functools import wraps


class CommonErrors:
	BAD_REQUEST = ({'error': 'Bad request.'}, 400)
	INVALID_ID = ({'error': 'Invalid id.'}, 404)
	NO_ACCESS = ({'error': 'Insufficient permissions.'}, 401)
	IM_A_TEAPOT = ({'error': 'I\'m a teapot.'}, 418)
	NOT_IMPLEMENTED = ({'error': 'Not implemented.'}, 501)


def authenticated(f):
	@wraps(f)
	def wrapper(*args, **kwargs):
		if current_user:
			if current_user.is_authenticated():
				try:
					ret = f(*args, **kwargs)
					db.session.commit()
					return ret
				except:
					db.session.rollback()
					db.session.close()
					raise
		return CommonErrors.NO_ACCESS
	return wrapper


def authenticated_web(f):
	@wraps(f)
	def wrapper(*args, **kwargs):
		if current_user:
			if current_user.is_authenticated():
				try:
					ret = f(*args, **kwargs)
					db.session.commit()
					return ret
				except:
					db.session.rollback()
					raise
		return abort(401)
	return wrapper

cache = Cache(config={'CACHE_TYPE': 'simple'})