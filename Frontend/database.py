from flask.ext.sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class User(db.Model):
	__tablename__ = 'PMSDB.users'
	id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.String, unique=True)
	ai_list = db.relationship("AI", order_by="AI.id", backref="User")

	def info(self):
		return {"id": self.id, "name": self.name, "ais": [ai.info() for ai in self.ai_list]}

	def __repr__(self):
		return "<User(id={}, name={})".format(self.id, self.name)

	# Flask.Login zeugs
	def is_authenticated(self):
		return True
	def is_active(self):
		return True
	def is_anonymous(self):
		return False
	def get_id(self):
		return self.name


ai_game_table = db.Table('PMSDB.ai_game', db.Model.metadata,
	db.Column('ai_id', db.Integer, db.ForeignKey('PMSDB.ais.id')),
	db.Column('game_id', db.Integer, db.ForeignKey('PMSDB.games.id'))
)


class AI(db.Model):
	__tablename__ = 'PMSDB.ais'
	id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.String, nullable=False)
	desc = db.Column(db.String)
	user_id = db.Column(db.Integer, db.ForeignKey('PMSDB.users.id'))
	user = db.relationship("User", backref=db.backref('PMSDB.ais', order_by=id))
	games = db.relationship("Game", order_by="Game.id", secondary=ai_game_table, backref="PMSDB.ais")

	def info(self):
		return {"id": self.id, "name": self.name, "author": self.user.name, "description": self.desc}

	def __repr__(self):
		return "<AI(id={}, name={}, user_id={}>".format(self.id, self.name, self.user_id)

class Game(db.Model):
	__tablename__ = 'PMSDB.games'
	id = db.Column(db.Integer, primary_key=True)
	ai_list = db.relationship("AI", order_by="AI.id", secondary=ai_game_table, backref="PMSDB.games")

	def info(self):
		return {"id": self.id, "ais": [ai.info() for ai in self.ai_list]}

	def __repr__(self):
		return "<Game(id={})>".format(self.id)


def create_mock_objects(count=10):
	import random
	users = [User(name="testuser"+str(i), id=i) for i in range(count)]
	users_left = users[:]
	def choose_user():
		u = random.choice(users_left)
		users_left.remove(u)
		return u
	ais = [AI(id=i, user=choose_user(), name="testai") for i in range(count)]
	games = [Game(id=i, ai_list=random.sample(ais, 2)) for i in range(count)]
	db.session.add_all(users + ais + games)
	db.session.commit()


if __name__ == '__main__':
	create_mock_objects()
	for user in db.query(User).all():
		print(user)
		print(user.info())