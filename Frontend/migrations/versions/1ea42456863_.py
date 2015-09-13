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
