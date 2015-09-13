####################################################################################
# 54ddadefaa_.py
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
