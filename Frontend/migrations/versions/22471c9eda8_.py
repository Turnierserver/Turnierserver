"""added 'visible' column

Revision ID: 22471c9eda8
Revises: afcacf5eb3
Create Date: 2015-11-07 10:50:49.629190

"""

# revision identifiers, used by Alembic.
revision = '22471c9eda8'
down_revision = 'afcacf5eb3'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_news', sa.Column('visible', sa.Boolean(), nullable=True))

def downgrade():
    op.drop_column('t_news', 'visible')
