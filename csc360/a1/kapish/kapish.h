#define _POSIX_C_SOURCE 200112L

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
#include <sys/wait.h>

#define MAX_BUF_BYTES 512
#define SHELL "kapish"

char* read_line();
char** parse_kapish(char*);
int execute_kapish();
void loop();
