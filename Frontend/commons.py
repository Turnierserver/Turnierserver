####################################################################################
# commons.py
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
			if current_user.is_authenticated:
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
			if current_user.is_authenticated:
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
