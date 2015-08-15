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
            ai.version.lang = ai.lang


def downgrade():
    op.drop_constraint(None, 't_ai_versions', type_='foreignkey')
    op.drop_column('t_ai_versions', 'lang_id')
