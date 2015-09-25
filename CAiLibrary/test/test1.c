#include "../properties.h"

#include <stdio.h>

int main ()
{
	char *text1 = "key\\= :\\=value\\n2ndval\\";
	char *text2 = "\tue";
	printf("%s\n%s\n", text1, text2);
	Element *elem = parseLine(text1, 0);
	elem = parseLine(text2, elem);
	printf("'%s'='%s'\n", elem->key, elem->value);
}
