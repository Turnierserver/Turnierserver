/*
 * properties.c
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
#include "properties.h"

#include <ctype.h>
#include <stdlib.h>
#include <string.h>

char* trim (char *s)
{
	// strip off the first whitespaces
	while (*s && isspace((unsigned char)(*s)))
		s++;
	if (!*s)
		return s;
	// strip off the last whitespaces
	char *p = s + strlen(s);
	while ((p > s) && isspace((unsigned char)(*(--p))))
		*p = 0;
	return s;
}

char* removeComment (char *s)
{
	// return the string until the first occurence of a ! or a #
	char *p = s;
	while (*p && (*p != '!') && (*p != '#'))
		p++;
	*p = 0;
	return s;
}

char* escape (const char *s)
{
	char *e = malloc(strlen(s) * 2 + 1); // worst case
	const char *p = s; char *r = e;
	while (*p)
	{
		if (*p == '\n')
		{
			*r = '\\';
			r++;
			*r = 'n';
		}
		else if (*p == '\r')
		{
			*r = '\\';
			r++;
			*r = 'r';
		}
		else if (*p == '\\')
		{
			*r = '\\';
			r++;
			*r = '\\';
		}
		else
			*r = *p;
		r++; p++;
	}
	*r = 0;
	return e;
}

char* unescape (const char *s, char *cont)
{
	char *u = malloc(strlen(s) + 1);
	const char *p = s; char *r = u;
	char esc = 0;
	while (*p)
	{
		if (esc)
		{
			if (*p == 'n')
				*r = '\n';
			else if (*p == 'r')
				*r = '\r';
			else
				*r = *p;
			r++;
			
			esc = 0;
		}
		else if (*p == '\\')
			esc = 1;
		else
		{
			*r = *p;
			r++;
		}
		p++;
	}
	*r = 0;
	if (cont)
		*cont = esc;
	return u;
}

Element* parseLine (const char *s, Element *prev)
{
	Element *elem = prev;
	if (elem && !elem->cont)
		elem = 0;
	if (!elem)
	{
		elem = malloc(sizeof(Element));
		elem->cont = 0;
		elem->key = 0;
		elem->value = 0;
	}
	
	char *l = malloc(strlen(s) + 1);
	strcpy(l, s);
	char *line = trim(removeComment(l));
	
	char *p = line;
	char esc = 0;
	// find the seperating char between the key and the value
	if (!elem->cont)
	{
		while (*p && (esc || (!isspace((unsigned char)(*p)) && (*p != '=') && (*p != ':'))))
		{
			if (*p == '\\')
				esc = 1;
			else
				esc = 0;
			p++;
		}
		// if the seperating key exists, write it into the element
		if (*p)
		{
			int diff = p - line;
			char *key = malloc(diff + 1);
			strncpy(key, line, diff);
			key[diff] = 0;
			elem->key = unescape(key, 0);
			free(key);
		}
		// else, write the line as the key, set the cont value and return
		else
		{
			elem->key = unescape(line, 0);
			free(l);
			elem->cont = esc;
			return elem;
		}
	}
	
	// remove any whitespace, = and : from the beginning of the value
	while (*p && (isspace((unsigned char)(*p)) || (*p == '=') || (*p == ':')))
		p++;
	// store the value
	char *value = malloc(strlen(line) - (p - line) + 1);
	strcpy(value, p);
	if (elem->cont)
	{
		char *val = malloc(strlen(value) + strlen(elem->value) + 1);
		strcpy(val, elem->value);
		strcat(val, value);
		free(elem->value);
		elem->value = val;
	}
	else
		elem->value = unescape(value, &(elem->cont));
	free(value);
	free(l);
	return elem;
}

Properties* parse (FILE *file)
{
	if (!file)
		return 0;
	
	Properties *p = malloc(sizeof(Properties));
	
	char *line = 0;
	Element *last = 0; size_t len = 0;
	while (getline(&line, &len, file) != -1)
	{
		last = parseLine(line, last);
		if (!last)
		{
			fprintf(stderr, "An error occured while parsing line %s\n", line);
			continue;
		}
		if (!last->key)
			last = 0;
		if (!last->value)
			last->value = "";
		if (!last || last->cont)
			continue;
		
		if (strcmp(last->key, "turnierserver.worker.host") == 0)
			p->host = last->value;
		else if (strcmp(last->key, "turnierserver.worker.server.port") == 0)
			p->port = last->value;
		else if (strcmp(last->key, "turnierserver.worker.server.aichar") == 0)
			p->aichar = last->value;
		else if (strcmp(last->key, "turnierserver.ai.uuid") == 0)
			p->uuid = last->value;
		else if (strcmp(last->key, "turnierserver.debug") == 0)
			p->debug = strcmp(last->value, "true") == 0 ? 1 : 0;
		else
			fprintf(stderr, "Unknown key: %s\n", last->key);
	}
	free(line);
	
	return p;
}
