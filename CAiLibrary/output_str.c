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
	uint size = 0;
	int j = i;
	while (j > 0)
	{
		j /= 10;
		size++;
	}
	char *s = malloc(size + (i<0 ? 2 : 1));
	char *p = s;
	if (i < 0)
	{
		*p = '-';
		p++;
	}
	for (int k = size - 1; k >= 0; k--)
	{
		*p = "0123456789" [(i / (int)pow(10, k)) % 10];
		p++;
	}
	*p = 0;
	return s;
}

char* dtos (double d)
{
	uint size = 0, digits = 0;
	int j = d;
	while (j > 0)
	{
		j /= 10;
		size++;
	}
	double e = (d - (long int)d);
	while (e != 0 && digits < 6)
	{
		e *= 10;
		e = (e - (int)e);
		digits++;
	}
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
		*p = "0123456789" [((long int)d / (long int)pow(10, k)) % 10];
		p++;
	}
	if (digits > 0)
	{
		*p = '.';
		p++;
		
		for (int k = 1; k <= digits; k++)
		{
			*p = "0123456789" [((long int)(d * (long int)pow(10, k))) % 10];
			p++;
		}
	}
	*p = 0;
	return s;
}
