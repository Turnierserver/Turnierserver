/*
 * qithubapi_global.h
 * 
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 * 
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 * 
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef QITHUBAPI_GLOBAL_H
#define QITHUBAPI_GLOBAL_H

#include <qglobal.h>

#if defined(QITHUBAPI_LIBRARY)
#  define QITHUBAPISHARED_EXPORT Q_DECL_EXPORT
#else
#  define QITHUBAPISHARED_EXPORT Q_DECL_IMPORT
#endif

#if defined(QITHUBAPI_DEBUG)
#  include <QDebug>
#  define QITHUBCALL(location) qDebug() << "Calling" << location
#else
#  define QITHUBCALL(location) Q_UNUSED(location)
#endif

#endif // QITHUBAPI_GLOBAL_H
