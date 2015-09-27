/*
 * test2.c
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
#include "../properties.c"

#include <stdio.h>

int main ()
{
	FILE *file = fopen("test2.prop", "r");
	if (!file)
	{
		printf("error opening file!\n");
		return 1;
	}
	Properties *p = parse(file);
	printf("host: %s\n", p->host);
	printf("port: %d\n", p->port);
}
