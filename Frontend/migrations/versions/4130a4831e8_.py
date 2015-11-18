"""added Game.tournament

Revision ID: 4130a4831e8
Revises: 22471c9eda8
Create Date: 2015-11-14 19:12:35.038462

"""

# revision identifiers, used by Alembic.
revision = '4130a4831e8'
down_revision = '22471c9eda8'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_games', sa.Column('tournament_id', sa.Integer(), nullable=True))
    op.create_foreign_key(None, 't_games', 't_tournaments', ['tournament_id'], ['id'])


def downgrade():
    op.drop_constraint(None, 't_games', type_='foreignkey')
    op.drop_column('t_games', 'tournament_id')
