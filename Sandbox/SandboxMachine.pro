#-------------------------------------------------
#
# Project created by QtCreator 2015-05-19T17:53:34
#
#-------------------------------------------------

QT       += core network
QT       -= gui

TARGET = sandboxd
CONFIG   += console
CONFIG   -= app_bundle

QMAKE_CXXFLAGS += -std=c++11

OBJECTS_DIR = obj/
MOC_DIR = gen/moc
RCC_DIR = gen/rc
UI_DIR  = gen/ui

TEMPLATE = app

INCLUDEPATH += include/

SOURCES += \
    src/main.cpp \
    src/workerclient.cpp \
    src/buffer.cpp \
    src/mirrorclient.cpp \
    src/global.cpp \
    src/aiexecutor.cpp \
    src/jobcontrol.cpp \
    src/logger.cpp

HEADERS += \
    include/workerclient.h \
    include/buffer.h \
    include/mirrorclient.h \
    include/global.h \
    include/aiexecutor.h \
    include/jobcontrol.h \
    include/logger.h
