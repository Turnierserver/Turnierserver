/*
 * properties.h
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef PROPERTIES_H
#define PROPERTIES_H

#include <stdio.h>
#include <sys/types.h>

#ifndef PROPERTIES_LINE_BUFFER
#  define PROPERTIES_LINE_BUFFER 200
#endif

#ifdef __cplusplus
extern "C" {
#endif

/// removes all leading and trailing whitespaces
char* trim (char *s);
/// escapes all \, \n and \r in s
char* escape (const char *s);
/// resolves all escaped chars in s, setting const[0] to 1 if s ends with a backslash
char* unescape (const char *s, char *cont);

struct _properties
{
	char *host;
	char *port;
	
	char *aichar;
	char *uuid;
	
	char debug; // will only be 0 (false) or 1 (true)
};
typedef struct _properties Properties;


struct _element
{
	char *key;
	char *value;
	char cont;
};
typedef struct _element Element;
Element* parseLine (const char *s, Element *prev);

Properties* parse (FILE *file);

#ifdef __cplusplus
}
#endif

#endif
