####################################################################################
# 4ad5d40f837_.py
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
"""Lang bei Versionen

Revision ID: 4ad5d40f837
Revises: 54ddadefaa
Create Date: 2015-08-15 20:37:46.263793

"""

# revision identifiers, used by Alembic.
revision = '4ad5d40f837'
down_revision = '54ddadefaa'

from alembic import op
import sqlalchemy as sa
from database import *


def upgrade():
    op.add_column('t_ai_versions', sa.Column('lang_id', sa.Integer(), nullable=True))
    op.create_foreign_key(None, 't_ai_versions', 't_langs', ['lang_id'], ['id'])
    for ai in AI.query:
        for version in ai.version_list:
            version.lang = ai.lang


def downgrade():
    op.drop_constraint(None, 't_ai_versions', type_='foreignkey')
    op.drop_column('t_ai_versions', 'lang_id')
