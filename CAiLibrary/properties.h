#ifndef PROPERTIES_H
#define PROPERTIES_H

#include <stdio.h>
#include <sys/types.h>

#ifndef PROPERTIES_LINE_BUFFER
#  define PROPERTIES_LINE_BUFFER 200
#endif

#ifdef __cplusplus
extern "C" {
#endif

/// removes all leading and trailing whitespaces
char* trim (char *s);
/// escapes all \, \n and \r in s
char* escape (char *s);
/// resolves all escaped chars in s, setting const[0] to 1 if s ends with a backslash
char* unescape (char *s, char *cont);

struct _properties
{
	char *host;
	char *port;
	
	char *aichar;
	char *uuid;
	
	char debug; // will only be 0 (false) or 1 (true)
};
typedef struct _properties Properties;


struct _element
{
	char *key;
	char *value;
	char cont;
};
typedef struct _element Element;
Element* parseLine (const char *s, Element *prev);

Properties* parse (FILE *file);

#ifdef __cplusplus
}
#endif

#endif
