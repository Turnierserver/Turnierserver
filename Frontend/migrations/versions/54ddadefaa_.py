"""reasons für Games

Revision ID: 54ddadefaa
Revises: 4097d4b6357
Create Date: 2015-07-30 14:20:01.246628

"""

# revision identifiers, used by Alembic.
revision = '54ddadefaa'
down_revision = '4097d4b6357'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_games', sa.Column('reason', sa.Text(), nullable=True))


def downgrade():
    op.drop_column('t_games', 'reason')
