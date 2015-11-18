/*
 * wrapper.c
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
#include "wrapper.h"

#include <math.h>
#include <netdb.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

char* readLine (Wrapper *w)
{
	uint bufsize = 8192;
	char *buf = calloc(bufsize + 1, 1), *p = buf;
	int read;
	while (1)
	{
		if ((read = recv(w->socketfd, p, 1, 0)) < 0)
			break;
		if (*p == '\n')
			break;
		bufsize += read;
		p += read;
		if (strlen(buf) >= bufsize)
		{
			bufsize += 8192;
			char *nbuf = calloc(bufsize + 1, 1);
			strcpy(nbuf, buf);
			p = nbuf + (p - buf);
			free(buf);
			buf = nbuf;
		}
	}
	return buf;
}

Wrapper *globalInit (int argc, char **argv)
{
	FILE *file = fopen(argc > 1 ? argv[1] : "ai.prop", "r");
	if (!file)
	{
		perror("Fehler beim Öffnen der KI-Properties");
		return 0;
	}	
	Wrapper *w = malloc(sizeof(Wrapper));
	w->p = parse(file);
	fclose(file);
	
	printf("Connecting to %s:%s\n", w->p->host, w->p->port);
	struct addrinfo hints;
	struct addrinfo *result, *rp = 0;
	int ret;
	// adresse suchen
	memset(&hints, 0, sizeof(struct addrinfo));
	hints.ai_family = AF_UNSPEC;    /* Allow IPv4 or IPv6 */
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = 0;
	hints.ai_protocol = 0;          /* Any protocol */
	if (ret = (getaddrinfo(w->p->host, w->p->port, &hints, &result)) != 0)
	{
		fprintf(stderr, "Fehler beim Nachschlagen von %s: %s\n", w->p->host, gai_strerror(ret));
		free(w->p);
		free(w);
		return 0;
	}
	// versuchen mit einer davon zu connecten
	for (rp = result; rp; rp = rp->ai_next)
	{
//		printf("Trying addr_sa_data=%s cannonname=%s family=%d protocol=%d socktype=%d\n", rp->ai_addr->sa_data, rp->ai_canonname, rp->ai_family, rp->ai_protocol, rp->ai_socktype);
		w->socketfd = socket(rp->ai_family, rp->ai_socktype, rp->ai_protocol);
		if (w->socketfd == -1)
			continue;
		if (connect(w->socketfd, rp->ai_addr, rp->ai_addrlen) != -1)
			break;
		close(w->socketfd);
	}
	// wenn rp=0 hat keine adresse funktioniert
	if (!rp)
	{
		fprintf(stderr, "Fehler beim Verbinden mit %s (port %s): %s\n", w->p->host, w->p->port, gai_strerror(ret));
		free(w->p);
		free(w);
		return 0;
	}
	freeaddrinfo(result);
	// "hallo" sagen
	char *msg = malloc(strlen(w->p->aichar) + strlen(w->p->uuid) + 2);
	strcpy(msg, w->p->aichar);
	strcat(msg, w->p->uuid);
	strcat(msg, "\n");
	if (write(w->socketfd, msg, strlen(msg)) == -1)
	{
		perror("Fehler beim Senden der UUID");
		close(w->socketfd);
		free(w->p);
		free(w);
		return 0;
	}
	
	return w;
}

void globalCleanup (Wrapper **w)
{
	close((*w)->socketfd);
	free((*w)->p);
	free(*w);
	*w = 0;
}

void __c_surrender (Wrapper *w)
{
	printf("sende surrender nachricht\n");
	char *msg = "SURRENDER\n";
	if (write(w->socketfd, msg, strlen(msg)) == -1)
		perror("Fehler beim Senden");
}
void surrender (Wrapper *w) { __c_surrender(w); }

void __c_crash (Wrapper *w, const char *reason)
{
	printf("sende crash nachricht\n");
	char *msg = malloc(strlen(reason) + 8);
	strcpy(msg, "CRASH ");
	strcat(msg, reason);
	strcat(msg, "\n");
	if (write(w->socketfd, msg, strlen(msg)) == -1)
		perror("Fehler beim Senden");
	free(msg);
}
void crash (Wrapper *w, const char *reason) { __c_crash(w, reason); }
