#include "catchsig.h"
#include "output.h"

#include <execinfo.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

Wrapper *currentWrapper = 0;

// http://stackoverflow.com/a/1925461/3755692
void crit_err_hdlr(int sig_num, siginfo_t *info, void *ucontext)
{
	void *             array[50];
	void *             caller_address;
	char **            messages;
	int                size, i;
	sig_ucontext_t *   uc;
	
	uc = (sig_ucontext_t *)ucontext;
	
	/* Get the address at the time the signal was raised */
	#if defined(__i386__) // gcc specific
		caller_address = (void *) uc->uc_mcontext.eip; // EIP: x86 specific
	#elif defined(__x86_64__) // gcc specific
		caller_address = (void *) uc->uc_mcontext.rip; // RIP: x86_64 specific
	#else
	#  error Unsupported architecture. // TODO: Add support for other arch.
	#endif
	
	fprintf(stderr, "signal %d (%s), address is %p from %p\n", 
			sig_num, strsignal(sig_num), info->si_addr, 
			(void *)caller_address);
	
	size = backtrace(array, 50);
	
	/* overwrite sigaction with caller's address */
	array[1] = caller_address;
	
	messages = backtrace_symbols(array, size);
	
	/* skip first stack frame (points here) */
	OutputBuffer *out = createBuffer();
	append(out, "CRITICAL: Catched signal "); appendi(out, sig_num); append(out, " ("); append(out, strsignal(sig_num)); append(out, ")\n");
	for (i = 1; i < size && messages != NULL; ++i)
	{
		printf("append (\n");
		append(out, "  (");
		printf("append %d\n", i);
		appendi(out, i);
		printf("append ) at\n");
		append(out, ") at ");
		printf("append %s\n", messages[i]);
		append(out, messages[i]);
		printf("append \\n\n");
		append(out, "\n");
	}
	const char *msg = readBuffer(out, OB_RETURN_REF);
	printf(msg);
	crash(currentWrapper, msg);
	printf("crash gesendet\n");
	destroyBuffer(out);
	
	free(messages);
	
	exit(1);
}

void registerSignalCatcher(Wrapper *w)
{
	struct sigaction sigact;
	
	sigact.sa_sigaction = crit_err_hdlr;
	sigact.sa_flags = SA_RESTART | SA_SIGINFO;
	
	if (sigaction(SIGSEGV, &sigact, (struct sigaction *)NULL) != 0)
	{
		fprintf(stderr, "error setting signal handler for %d (%s)\n",
				SIGSEGV, strsignal(SIGSEGV));
		
		exit(EXIT_FAILURE);
	}
	
	if (w)
		currentWrapper = w;
}
