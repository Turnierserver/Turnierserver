""" Turnier in DB

Revision ID: b2c1b11419
Revises: 57012d78bcd
Create Date: 2015-10-01 21:38:53.934861

"""

# revision identifiers, used by Alembic.
revision = 'b2c1b11419'
down_revision = '57012d78bcd'

from alembic import op
import sqlalchemy as sa
from flask.ext.sqlalchemy import SQLAlchemy

db = SQLAlchemy(session_options={"expire_on_commit": False})

def upgrade():
	db.engine.execute("""CREATE TABLE t_tournaments
(
  id serial NOT NULL,
  name text NOT NULL,
  "timestamp" bigint,
  type_id integer,
  executed boolean NOT NULL DEFAULT false,
  CONSTRAINT t_tournaments_pkey PRIMARY KEY (id),
  CONSTRAINT t_tournaments_type_id_fkey FOREIGN KEY (type_id)
      REFERENCES t_gametypes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT t_tournaments_name_key UNIQUE (name)
);""")
	db.engine.execute("""CREATE TABLE t_tournament_games
(
  id serial NOT NULL,
  tournament_id integer,
  game_id integer,
  CONSTRAINT t_tournament_games_pkey PRIMARY KEY (id),
  CONSTRAINT t_tournament_games_game_id_fkey FOREIGN KEY (game_id)
      REFERENCES t_games (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT t_tournament_games_tournament_id_fkey FOREIGN KEY (tournament_id)
      REFERENCES t_tournaments (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);""")
	db.engine.execute("""CREATE TABLE t_user_tournament_ais
(
  id serial NOT NULL,
  user_id integer,
  ai_id integer,
  type_id integer,
  CONSTRAINT t_user_tournament_ais_pkey PRIMARY KEY (id),
  CONSTRAINT t_user_tournament_ais_ai_id_fkey FOREIGN KEY (ai_id)
      REFERENCES t_ais (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT t_user_tournament_ais_type_id_fkey FOREIGN KEY (type_id)
      REFERENCES t_gametypes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT t_user_tournament_ais_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES t_users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)""")
	op.add_column('t_games', sa.Column('tournament_game_id', sa.INTEGER(), autoincrement=False, nullable=True))
	op.create_foreign_key('t_games_tournament_game_id_fkey', 't_games', 't_tournament_games', ['tournament_game_id'], ['id'])


def downgrade():
	op.drop_constraint('t_games_tournament_game_id_fkey', 't_games', type_='foreignkey')
	op.drop_column('t_games', 'tournament_game_id')
	db.engine.execute("DROP TABLE t_user_tournament_ais;")
	db.engine.execute("DROP TABLE t_tournament_games;")
	db.engine.execute("DROP TABLE t_tournaments;")
