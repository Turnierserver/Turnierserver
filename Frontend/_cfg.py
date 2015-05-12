
class LocalEnvironment:
	db_url = "sqlite:///test.db"
	secret_key = "foobar"
	airbrake = False
	airbrake_key = None
	airbrake_id = None

env = LocalEnvironment