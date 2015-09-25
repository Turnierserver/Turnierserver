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
char* toString (int i);

Wrapper *globalInit (int argc, char **argv);
void globalCleanup (Wrapper **w);

void surrender (Wrapper *w);
void crash (Wrapper *w, const char *reason);

#ifdef __cplusplus
}
#endif

#endif
