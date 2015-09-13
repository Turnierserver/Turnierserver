####################################################################################
# 4097d4b6357_.py
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
