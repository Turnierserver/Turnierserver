"""keine vizs mehr in der db

Revision ID: 4097d4b6357
Revises: 1ea42456863
Create Date: 2015-07-23 15:29:57.471248

"""

# revision identifiers, used by Alembic.
revision = '4097d4b6357'
down_revision = '1ea42456863'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.drop_column('t_gametypes', 'viz')


def downgrade():
    op.add_column('t_gametypes', sa.Column('viz', sa.TEXT(), autoincrement=False, nullable=False))
