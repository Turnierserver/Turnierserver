
class LocalEnvironment:
	airbrake = False
	airbrake_key = None
	airbrake_id = None
	ftp_url = "127.0.0.1"
	ftp_uname = "user"
	ftp_pw = "pw"
	backend_port = 1333
	backend_url = "127.0.0.1"
	cache_max_age = 60
	web_port = 5000
	clean_db = True
	ssl = False

	##Flask cfg-obj
	DEBUG = True
	PROPAGATE_EXCEPTIONS=True
	SECRET_KEY = "foobar"
	MAX_CONTENT_LENGTH=1024*1024
	SQLALCHEMY_DATABASE_URI = "sqlite:///test.db"
	SQLALCHEMY_ECHO = False


def showopts():
	for key in dir(env):
		if not key.startswith("__"):
			print("{} = {}".format(key, getattr(env, key)))

if __name__ == "__main__":
	showopts()
