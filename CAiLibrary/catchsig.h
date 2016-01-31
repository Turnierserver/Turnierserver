#ifndef CATCHSIG_H
#define CATCHSIG_H

#include "wrapper.h"

#include <ucontext.h>

/* This structure mirrors the one found in /usr/include/asm/ucontext.h */
typedef struct _sig_ucontext
{
	unsigned long     uc_flags;
	struct ucontext   *uc_link;
	stack_t           uc_stack;
	struct sigcontext uc_mcontext;
	sigset_t          uc_sigmask;
} sig_ucontext_t;

extern void registerSignalCatcher(Wrapper* w);

#endif
