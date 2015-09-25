#include "../wrapper.h"

int main (int argc, char **argv)
{
	Wrapper *w = globalInit(argc, argv);
	if (w)
		printf("Wrapper successfully created\n");
	globalCleanup(w);
}
