/*
 * logger.h
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

#ifndef LOGGER_H
#define LOGGER_H

#include <QtGlobal>
#include <QVariant>

class Logger
{
	Q_DISABLE_COPY(Logger)
	
public:
	enum Category
	{
		INFO,
		DEBUG,
		WARNING,
		CRITICAL
	};
	
	Logger(const char *file, int line, const char *function, Category category = INFO);
	
	void operator << (const QVariant &variant) const;
	
private:
	const char *_file;
	int _line;
	const char *_function;
	Category _category;
	
};

#ifdef LOG_INFO
#  undef LOG_INFO
#endif
#define LOG_INFO Logger(__FILE__, __LINE__, __FUNCTION__, Logger::INFO)

#ifdef LOG_DEBUG
#  undef LOG_DEBUG
#endif
#define LOG_DEBUG Logger(__FILE__, __LINE__, __FUNCTION__, Logger::DEBUG)

#ifdef LOG_WARNING
#  undef LOG_WARNING
#endif
#define LOG_WARNING Logger(__FILE__, __LINE__, __FUNCTION__, Logger::WARNING)

#ifdef LOG_CRITICAL
#  undef LOG_CRITICAL
#endif
#define LOG_CRITICAL Logger(__FILE__, __LINE__, __FUNCTION__, Logger::CRITICAL)

#endif // LOGGER_H
