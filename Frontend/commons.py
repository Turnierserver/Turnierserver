from database import db
from flask import abort
from flask.ext.login import current_user
from flask.ext.cache import Cache
from functools import wraps


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
					db.session.close()
					raise
		abort(401) ##TODO: ne 401 error page
	return wrapper


cache = Cache(config={'CACHE_TYPE': 'simple'})