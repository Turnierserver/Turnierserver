####################################################################################
# 5ab0bdeeeaa_.py
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
