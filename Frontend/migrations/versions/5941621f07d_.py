####################################################################################
# 5941621f07d_.py
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
"""Libraries richtig in der DB

Revision ID: 5941621f07d
Revises: 3a6f28e748b
Create Date: 2015-08-20 21:26:25.820875

"""

# revision identifiers, used by Alembic.
revision = '5941621f07d'
down_revision = '3a6f28e748b'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.create_table('t_libraries',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.Text(), nullable=False),
    sa.Column('display_name', sa.Text(), nullable=False),
    sa.Column('lang_id', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['lang_id'], ['t_langs.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('t_ai_versions_libraries_assoc',
    sa.Column('ai_version_id', sa.Integer(), nullable=True),
    sa.Column('library_id', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['ai_version_id'], ['t_ai_versions.id'], ),
    sa.ForeignKeyConstraint(['library_id'], ['t_libraries.id'], )
    )
    op.drop_column('t_ai_versions', 'extras_str')


def downgrade():
    op.add_column('t_ai_versions', sa.Column('extras_str', sa.TEXT(), autoincrement=False, nullable=True))
    op.drop_table('t_ai_versions_libraries_assoc')
    op.drop_table('t_libraries')
