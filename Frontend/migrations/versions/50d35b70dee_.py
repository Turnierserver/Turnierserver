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
