import logging

hellblau = "\033[36m"
level_strings = {
	logging.DEBUG: "\033[1mDEBUG ",
	logging.INFO: "\033[1mINFO ",
	logging.WARNING: "\033[1;33mWARNING ",
	logging.ERROR: "\033[1;31mERROR ",
	logging.CRITICAL: "\033[1;31mCRITICAL "
}
normal = "\033[0m"
in_str = "\033[0min\033[1;32m"
logger = logging.getLogger('Frontend')
logger.setLevel(logging.DEBUG)
fh = logging.FileHandler('frontend.log')
fh.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)

class ColorfulFormatter(logging.Formatter):
	orig_format = hellblau + "[%(asctime)s] {}" + in_str + " %(module)s (%(filename)s:%(lineno)d) " + normal + "%(message)s"

	def format(self, record):
		s = level_strings.get(record.levelno, "\033[1;31mUNKNOWN_LEVEL("+str(record.levelno)+")")
		self._fmt = self.orig_format.format(s)
		self._style = logging.PercentStyle(self._fmt)
		return logging.Formatter.format(self, record)

formatter = ColorfulFormatter()
fh.setFormatter(formatter)
ch.setFormatter(formatter)

logger.addHandler(fh)
logger.addHandler(ch)

from database import db
from flask import abort
from flask.ext.login import current_user
from flask.ext.cache import Cache
from functools import wraps


class CommonErrors:
	BAD_REQUEST = ({'error': 'Bad request.'}, 400)
	INVALID_ID = ({'error': 'Invalid id.'}, 404)
	NO_ACCESS = ({'error': 'Insufficient permissions.'}, 401)
	IM_A_TEAPOT = ({'error': 'I\'m a teapot.'}, 418)
	NOT_IMPLEMENTED = ({'error': 'Not implemented.'}, 501)
	FTP_ERROR = ({'error': 'FTP-Error'}, 503)


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
