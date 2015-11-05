"""Tournament stuff, news

Revision ID: 15964d8264e
Revises: 2dde875c0ee
Create Date: 2015-11-05 23:40:53.388980

"""

# revision identifiers, used by Alembic.
revision = '15964d8264e'
down_revision = '2dde875c0ee'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.create_table('t_news',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('last_edited', sa.BigInteger(), nullable=True),
        sa.Column('text', sa.Text(), nullable=False),
        sa.PrimaryKeyConstraint('id')
    )
    op.drop_constraint('t_games_tournament_game_id_fkey', 't_games', type_='foreignkey')
    op.drop_column('t_games', 'tournament_game_id')
    op.drop_constraint('t_tournaments_name_key', 't_tournaments', type_='unique')


def downgrade():
    op.create_unique_constraint('t_tournaments_name_key', 't_tournaments', ['name'])
    op.add_column('t_games', sa.Column('tournament_game_id', sa.INTEGER(), autoincrement=False, nullable=True))
    op.create_foreign_key('t_games_tournament_game_id_fkey', 't_games', 't_tournament_games', ['tournament_game_id'], ['id'])
    op.drop_table('t_news')
