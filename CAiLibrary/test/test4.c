/*
 * test4.c
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
#include "../output.h"

#include <stdio.h>

int main ()
{
	int num = 1234567890;
	printf("number: %d\n", num);
	printf("string: %s\n", itos(num));
	double real = 13245.6789;
	printf("number: %f\n", real);
	printf("string: %s\n", dtos(real));

	OutputBuffer *buf = createBuffer();
	append(buf, "number: ");
	appendi(buf, num);
	append(buf, "\nfloat: ");
	appendd(buf, real);
	append(buf, "\n");
	printf("buffer:\n\n%s\n", readBuffer(buf, 0));
	destroyBuffer(buf);
}
