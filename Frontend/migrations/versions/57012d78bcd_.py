####################################################################################
# 57012d78bcd_.py
#
# Copyright (C) 2015 Pixelgaffer
#
# This work is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as published by the
# Free Software Foundation; either version 2 of the License, or any later
# version.
#
# This work is distributed in the hope that it will be useful, but without
# any warranty; without even the implied warranty of merchantability or
# fitness for a particular purpose. See version 2 and version 3 of the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
####################################################################################
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
