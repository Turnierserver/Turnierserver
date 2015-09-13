####################################################################################
# 1ea42456863_.py
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
"""'name_public' bei Nutzern

Revision ID: 1ea42456863
Revises: 11fa1cbd462
Create Date: 2015-07-18 10:09:57.859564

"""

# revision identifiers, used by Alembic.
revision = '1ea42456863'
down_revision = '11fa1cbd462'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_users', sa.Column('name_public', sa.Boolean(), nullable=True))


def downgrade():
    op.drop_column('t_users', 'name_public')
