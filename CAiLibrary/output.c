/*
 * output.c
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
#include "output.h"

#include <stdlib.h>
#include <string.h>

OutputBuffer* createBuffer ()
{
	OutputBuffer *buf = malloc(sizeof(OutputBuffer));
	buf->buf = malloc(1);
	buf->buf[0] = 0;
	return buf;
}

char* readBuffer (OutputBuffer *buf, unsigned char mode)
{
	char *cbuf = buf->buf;
	if ((mode & OB_RETURN_COPY) != 0)
	{
		cbuf = malloc(strlen(buf->buf) + 1);
		strcpy(cbuf, buf->buf);
	}
	if ((mode & OB_CLEAR_STR) != 0)
	{
		if ((mode & OB_RETURN_COPY) != 0)
			free(buf->buf);
		buf->buf = malloc(1);
		buf->buf[0] = 0;
	}
	return cbuf;
}

void destroyBuffer (OutputBuffer *buf)
{
	if (buf->buf)
		free(buf->buf);
	free(buf);
}

OutputBuffer* appendi (OutputBuffer *buf, int i)
{
	char *str = itos(i);
	append(buf, str);
	free(str);
	return buf;
}

OutputBuffer* appendd (OutputBuffer *buf, double d)
{
	char *str = dtos(d);
	append(buf, str);
	free(str);
	return buf;
}

OutputBuffer* append (OutputBuffer *buf, char *str)
{
	char *nbuf = malloc(strlen(buf->buf) + strlen(str) + 1);
	strcpy(nbuf, buf->buf);
	strcat(nbuf, str);
	nbuf[strlen(buf->buf) + strlen(str)] = 0;
	free(buf->buf);
	buf->buf = nbuf;
	return buf;
}
