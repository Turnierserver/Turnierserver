####################################################################################
# 11fa1cbd462_.py
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
"""AI_Game_Assocs haben jetzt eine Punktzahl

Revision ID: 11fa1cbd462
Revises: 1aba699734a
Create Date: 2015-06-13 12:12:35.416914

"""

# revision identifiers, used by Alembic.
revision = '11fa1cbd462'
down_revision = '1aba699734a'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_ai_game_assocs', sa.Column('score', sa.Integer(), nullable=True))


def downgrade():
    op.drop_column('t_ai_game_assocs', 'score')
