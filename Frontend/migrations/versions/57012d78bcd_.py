"""compiler und interpreter

Revision ID: 57012d78bcd
Revises: 50d35b70dee
Create Date: 2015-09-10 23:10:20.784621

"""


revision = '57012d78bcd'
down_revision = '50d35b70dee'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_langs', sa.Column('compiler', sa.Text(), nullable=True))
    op.add_column('t_langs', sa.Column('interpreter', sa.Text(), nullable=True))


def downgrade():
    op.drop_column('t_langs', 'interpreter')
    op.drop_column('t_langs', 'compiler')
