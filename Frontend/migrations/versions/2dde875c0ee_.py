"""Speichern ob das Turnier fertig ist

Revision ID: 2dde875c0ee
Revises: b2c1b11419
Create Date: 2015-10-02 19:29:47.431171

"""

# revision identifiers, used by Alembic.
revision = '2dde875c0ee'
down_revision = 'b2c1b11419'

from alembic import op
import sqlalchemy as sa


def upgrade():
	op.add_column('t_tournaments', sa.Column('finished', sa.BOOLEAN(), server_default=sa.text('false'), autoincrement=False, nullable=False))


def downgrade():
	op.drop_column('t_tournaments', 'finished')

