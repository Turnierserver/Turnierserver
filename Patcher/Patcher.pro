TEMPLATE = subdirs

CONFIG += ordered

SUBDIRS += \
    QitHubAPI \
    Patcher

Patcher.depends = QitHubAPI
