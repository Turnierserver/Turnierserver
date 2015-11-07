/*
 * output_str.c
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

#include <math.h>
#include <stdlib.h>
#include <sys/types.h>

char* itos (int i)
{
#ifdef OUTPUT_DEBUG
	printf("--- invoking itos(%d) ---\n", i);
#endif
	uint u = (i<0 ? i*-1 : i);
	uint size = 0;
	uint j = u;
	while (j > 0)
	{
		j /= 10;
		size++;
	}
	if (size < 1)
		size = 1;
#ifdef OUTPUT_DEBUG
	printf(" - size=%d, minus= ==> malloc(%d)\n", size, (size + (i<0 ? 2 : 1)));
#endif
	char *s = malloc(size + (i<0 ? 2 : 1));
	char *p = s;
	if (i < 0)
	{
		*p = '-';
		p++;
	}
	for (int k = size - 1; k >= 0; k--)
	{
		*p = "0123456789" [(u / (int)pow(10, k)) % 10];
		p++;
	}
	*p = 0;
#ifdef OUTPUT_DEBUG
	printf("--- finished itos(%d): %s ---\n", i, s);
#endif
	return s;
}

char* dtos (double d)
{
#ifdef OUTPUT_DEBUG
	printf("--- invoking dtos(%f) ---\n", d);
#endif
	double u = (d<0 ? d*-1 : d); // there is no udouble or stuff like that
	uint size = 0, digits = 0;
	long int j = (int)u;
	while (j > 0)
	{
		j /= 10;
		size++;
	}
	if (size < 1)
		size = 1;
	double e = (u - (long int)u);
	while (e != 0 && digits < 6)
	{
		e *= 10;
		e = (e - (int)e);
		digits++;
	}
#ifdef OUTPUT_DEBUG
	printf("- size=%d, digits=%d ==> malloc(%d)\n", size, digits, (size + (d<0 ? 1 : 0) + digits + (digits>0 ? 1 : 0) + 1));
#endif
	char *s = malloc(
				size + // vorkommastellen
				(d<0 ? 1 : 0) + // vorzeichen
				digits + // nachkommastellen
				(digits>0 ? 1 : 0) + // punkt
				1 // \0
				);
	char *p = s;
	if (d < 0)
	{
		*p = '-';
		p++;
	}
	for (int k = size - 1; k >= 0; k--)
	{
		*p = "0123456789" [((long int)u / (long int)pow(10, k)) % 10];
		p++;
	}
	if (digits > 0)
	{
		*p = '.';
		p++;
		
		for (int k = 1; k <= digits; k++)
		{
			*p = "0123456789" [((long int)(u * (long int)pow(10, k))) % 10];
			p++;
		}
	}
	*p = 0;
#ifdef OUTPUT_DEBUG
	printf("--- finished dtos(%f): %s ---\n", d, s);
#endif
	return s;
}
