import logging
from _cfg import env

hellblau = "\033[36m"
level_strings = {
	logging.DEBUG: ("\033[1m", "DEBUG"),
	logging.INFO: ("\033[1m", "INFO"),
	logging.WARNING: ("\033[1;33m", "WARNING"),
	logging.ERROR: ("\033[1;31m", "ERROR"),
	logging.CRITICAL: ("\033[1;31m", "CRITICAL")
}
level_strings_len = max([len(level_strings[key][1]) for key in level_strings])
for key in level_strings:
	color, text = level_strings[key]
	text = "{:>{}}".format(text, level_strings_len)
	level_strings[key] = (color, text)

normal = "\033[0m"
in_str = "\033[0min\033[1;32m"
logger = logging.getLogger('Frontend')
logger.setLevel(logging.DEBUG)
logger.propagate = False
fh = logging.FileHandler('debug.log')
fh.setLevel(logging.DEBUG)
fh_wrn = logging.FileHandler('warning.log')
fh_wrn.setLevel(logging.WARNING)
fh_err = logging.FileHandler('error.log')
fh_err.setLevel(logging.ERROR)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)

class ColorfulFormatter(logging.Formatter):
	orig_format = hellblau + "[%(asctime)s]" + normal + " {}" + in_str + " %(funcName)s (%(filename)s:%(lineno)d)" + normal + " %(message)s"

	def format(self, record):
		s = level_strings.get(record.levelno, "\033[1;31mUNKNOWN_LEVEL("+str(record.levelno)+")")
		self._fmt = self.orig_format.format(s[0] + s[1] + " ")
		self._style = logging.PercentStyle(self._fmt)
		return logging.Formatter.format(self, record)

formatter = ColorfulFormatter()
formatter.datefmt = "%d.%m %H:%M:%S"
fh.setFormatter(formatter)
fh_err.setFormatter(formatter)
fh_wrn.setFormatter(formatter)
ch.setFormatter(formatter)

logger.addHandler(fh)
logger.addHandler(fh_err)
logger.addHandler(fh_wrn)
logger.addHandler(ch)


formatter = logging.Formatter(hellblau + "[%(asctime)s]" + normal + " WERKZEUG" + normal + " %(message)s")
formatter.datefmt = "%d.%m %H:%M:%S"
wl = logging.getLogger('werkzeug')
wl.propagate = False
wl.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
ch.setFormatter(formatter)
wl.addHandler(ch)

if env.SENTRY:
	from raven.handlers.logging import SentryHandler
	sentryHandler = SentryHandler(env.SENTRY_DSN)
	sentryHandler.setLevel(logging.WARNING)
	logger.addHandler(sentryHandler)

import werkzeug.serving
from werkzeug._internal import _log
WSGIRequestHandler = werkzeug.serving.WSGIRequestHandler

def log(self, type, message, *args):
	_log(type, '%s - %s\n' % (self.address_string(),
								message % args))
WSGIRequestHandler.log = log
werkzeug.serving.WSGIRequestHandler = WSGIRequestHandler
