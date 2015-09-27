/*
 * wrapper.h
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
#ifndef WRAPPER_H
#define WRAPPER_H

#include "properties.h"

#ifndef MAX_BUFFSIZE
#  define MAX_BUFFSIZE 10485760 /* 10 Mib */
#endif

#ifdef __cplusplus
extern "C" {
#endif

struct _wrapper
{
	Properties *p;
	int socketfd;
};
typedef struct _wrapper Wrapper;

char* readLine (Wrapper *w);

Wrapper *globalInit (int argc, char **argv);
void globalCleanup (Wrapper **w);

void surrender (Wrapper *w);
void crash (Wrapper *w, const char *reason);

#ifdef __cplusplus
}
#endif

#endif
