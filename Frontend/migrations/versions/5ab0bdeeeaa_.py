"""crashes bei Spielen speichern

Revision ID: 5ab0bdeeeaa
Revises: 4ad5d40f837
Create Date: 2015-08-15 22:51:36.719551

"""

# revision identifiers, used by Alembic.
revision = '5ab0bdeeeaa'
down_revision = '4ad5d40f837'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_games', sa.Column('_crashes', sa.Text(), nullable=True))


def downgrade():
    op.drop_column('t_games', '_crashes')
