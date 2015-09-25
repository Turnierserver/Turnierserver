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
