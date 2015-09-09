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
#include <unistd.h>

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
	bool escapeCodes = isatty(STDOUT_FILENO) == 1;
	if (escapeCodes)
		fprintf(stderr, "\033[36m");
	fprintf(stderr, "[%s] ", qPrintable(QDateTime::currentDateTime().toString("dd.MM.yy hh:mm:ss.zzz t")));
	if (escapeCodes)
	{
		fprintf(stderr, "\033[0m");
		switch (_category)
		{
		case INFO:     fprintf(stderr, "\033[1mINFO "); break;
		case DEBUG:    fprintf(stderr, "\033[1mDEBUG "); break;
		case WARNING:  fprintf(stderr, "\033[1;33mWARNING "); break;
		case CRITICAL: fprintf(stderr, "\033[1;31mCRITICAL "); break;
		}
		fprintf(stderr, "\033[0min \033[1;32m%s\033[0m \033[32m(%s:%d)\033[0m", qPrintable(_function), qPrintable(_file), _line);
	}
	else
	{
		switch (_category)
		{
		case INFO:     fprintf(stderr, "INFO     "); break;
		case DEBUG:    fprintf(stderr, "DEBUG    "); break;
		case WARNING:  fprintf(stderr, "WARNING  "); break;
		case CRITICAL: fprintf(stderr, "CRITICAL "); break;
		}
		fprintf(stderr, "in %s (%s:%d)", qPrintable(_function), qPrintable(_file), _line);
	}
	fprintf(stderr, ": %s\n", qPrintable(variant.toString()));
}
