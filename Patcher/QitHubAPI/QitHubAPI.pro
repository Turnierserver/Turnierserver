#-------------------------------------------------
#
# Project created by QtCreator 2015-06-06T13:01:50
#
#-------------------------------------------------

QT       += core network
QT       -= gui

TARGET = ../QitHubAPI
TEMPLATE = lib

QMAKE_CXXFLAGS += -std=c++11

DEFINES += QITHUBAPI_LIBRARY
#DEFINES += QITHUBAPI_DEBUG

SOURCES += \
    src/qithubapi.cpp \
    src/qithubrepository.cpp \
    src/qithubbranch.cpp \
    src/qithubcommit.cpp \
    src/qithubfile.cpp

INCLUDEPATH += include/

HEADERS +=\
    include/qithubapi.h \
    include/qithubapi_global.h \
    include/qithubrepository.h \
    include/qithubbranch.h \
    include/qithubcommit.h \
    include/qithubfile.h

unix {
    target.path = /usr/local/lib
    INSTALLS += target
}
