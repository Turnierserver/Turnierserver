####################################################################################
# 50d35b70dee_.py
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
"""_active_version ist jetzt da

Revision ID: 50d35b70dee
Revises: 5941621f07d
Create Date: 2015-09-09 18:17:18.086207

"""

# revision identifiers, used by Alembic.
revision = '50d35b70dee'
down_revision = '5941621f07d'

from alembic import op
import sqlalchemy as sa


def upgrade():
	op.add_column('t_ais', sa.Column('_active_version_id', sa.Integer(), sa.ForeignKey("t_ai_versions.id"), nullable=True))


def downgrade():
	op.drop_column('t_ais', '_active_version_id')
