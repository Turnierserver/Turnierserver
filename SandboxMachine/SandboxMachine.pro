#-------------------------------------------------
#
# Project created by QtCreator 2015-05-19T17:53:34
#
#-------------------------------------------------

QT       += core network
QT       -= gui

TARGET = SandboxMachine
CONFIG   += console
CONFIG   -= app_bundle

QMAKE_CXXFLAGS += -std=c++11

TEMPLATE = app

INCLUDEPATH += include/

SOURCES += \
    src/main.cpp \
    src/workerclient.cpp \
    src/buffer.cpp

HEADERS += \
    include/workerclient.h \
    include/buffer.h
