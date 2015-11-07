/*
 * output.h
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
#ifndef OUTPUT_H
#define OUTPUT_H

#if CMAKE_BUILD_TYPE == Debug
#  include <stdio.h>
#  define OUTPUT_DEBUG
#endif

char* itos (int i);
char* dtos (double d);

struct _output_buffer
{
	char *buf;
};
typedef struct _output_buffer OutputBuffer;

#define OB_KEEP_STR 0
#define OB_CLEAR_STR 1
#define OB_RETURN_REF 0
#define OB_RETURN_COPY 2

OutputBuffer* createBuffer ();
char* readBuffer (OutputBuffer *buf, unsigned char mode);
void destroyBuffer (OutputBuffer *buf);

OutputBuffer* appendi (OutputBuffer *buf, int i);
OutputBuffer* appendd (OutputBuffer *buf, double d);
OutputBuffer* append (OutputBuffer *buf, char *str);

#endif
