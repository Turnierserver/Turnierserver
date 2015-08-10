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

unix: {
	LIBS += ../libQitHubAPI.so
	PRE_TARGETDEPS += ../libQitHubAPI.so
}
else: {
	LIBS += ../QitHubAPI.dll
	PRE_TARGETDEPS += ../QitHubAPI.dll
}
INCLUDEPATH += ../../Patcher/QitHubAPI/include/

LIBS += -larchive

INCLUDEPATH += include/

SOURCES += \
    src/main.cpp \
    src/patcher.cpp \
    src/module.cpp \
    src/upload.cpp

HEADERS += \
    include/patcher.h \
    include/module.h \
    include/upload.h
