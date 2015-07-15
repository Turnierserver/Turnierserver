/*
 * logger.cpp
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

#include "logger.h"

#include <stdio.h>

#include <QDateTime>

Logger::Logger(const char *file, int line, const char *function, Category category)
	: _file(file)
	, _line(line)
	, _function(function)
	, _category(category)
{
}

void Logger::operator << (const QVariant &variant) const
{
	fprintf(stderr, "[%s] ", qPrintable(QDateTime::currentDateTime().toString("dd.MM.yy hh:mm:ss.zzz t")));
	switch (_category)
	{
	case INFO:     fprintf(stderr, "INFO:     "); break;
	case DEBUG:    fprintf(stderr, "DEBUG:    "); break;
	case WARNING:  fprintf(stderr, "WARNING:  "); break;
	case CRITICAL: fprintf(stderr, "CRITICAL: "); break;
	}
	fprintf(stderr, "%s\n", qPrintable(variant.toString()));
}
