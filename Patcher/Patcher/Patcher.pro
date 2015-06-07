#-------------------------------------------------
#
# Project created by QtCreator 2015-06-06T13:30:38
#
#-------------------------------------------------

QT       += core network
QT       -= gui

TARGET = ../patcher
CONFIG   += console
CONFIG   -= app_bundle

TEMPLATE = app

QMAKE_CXXFLAGS += -std=c++11

LIBS += ../libQitHubAPI.so
PRE_TARGETDEPS += ../libQitHubAPI.so
INCLUDEPATH += ../../Patcher/QitHubAPI/include/

SOURCES += main.cpp
