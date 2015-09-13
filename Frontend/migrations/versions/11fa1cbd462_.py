"""AI_Game_Assocs haben jetzt eine Punktzahl

Revision ID: 11fa1cbd462
Revises: 1aba699734a
Create Date: 2015-06-13 12:12:35.416914

"""

# revision identifiers, used by Alembic.
revision = '11fa1cbd462'
down_revision = '1aba699734a'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_ai_game_assocs', sa.Column('score', sa.Integer(), nullable=True))


def downgrade():
    op.drop_column('t_ai_game_assocs', 'score')
