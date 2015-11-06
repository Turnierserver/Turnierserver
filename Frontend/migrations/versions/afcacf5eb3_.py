"""news foreign keys

Revision ID: afcacf5eb3
Revises: 15964d8264e
Create Date: 2015-11-06 00:30:57.151674

"""

# revision identifiers, used by Alembic.
revision = 'afcacf5eb3'
down_revision = '15964d8264e'

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.add_column('t_news', sa.Column('last_edited_by_id', sa.Integer(), nullable=True))
    op.create_foreign_key(None, 't_news', 't_users', ['last_edited_by_id'], ['id'])


def downgrade():
    op.drop_constraint(None, 't_news', type_='foreignkey')
    op.drop_column('t_news', 'last_edited_by_id')
