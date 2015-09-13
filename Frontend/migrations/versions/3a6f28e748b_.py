"""DB Aufräumen und AI_GAME_ASSOC.position

Revision ID: 3a6f28e748b
Revises: 5ab0bdeeeaa
Create Date: 2015-08-16 11:34:50.749469

"""

# revision identifiers, used by Alembic.
revision = '3a6f28e748b'
down_revision = '5ab0bdeeeaa'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.drop_table('t_gametyperoles')
    op.add_column('t_ai_game_assocs', sa.Column('calculationPoints', sa.Integer(), nullable=True))
    op.add_column('t_ai_game_assocs', sa.Column('position', sa.Integer(), nullable=True))


def downgrade():
    op.drop_column('t_ai_game_assocs', 'position')
    op.drop_column('t_ai_game_assocs', 'calculationPoints')
    op.create_table('t_gametyperoles',
    sa.Column('id', sa.INTEGER(), nullable=False),
    sa.Column('name', sa.TEXT(), autoincrement=False, nullable=False),
    sa.Column('gametype_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.ForeignKeyConstraint(['gametype_id'], ['t_gametypes.id'], name='t_gametyperoles_gametype_id_fkey'),
    sa.PrimaryKeyConstraint('id', name='t_gametyperoles_pkey')
    )
