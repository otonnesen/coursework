/*
 * UVic SENG 265, Summer 2018, A#4
 *
 * This will contain a solution to uvroff2.c. In order to complete the
 * task of formatting a file, it must open the file and pass the result
 * to a routine in formatter.c.
 */
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "formatter.h"

FILE *input;

int main(int argc, char *argv[]) {
	input = fopen(argv[1], "r");
	char** lines;

	lines = input == NULL ? format_lines() : format_file(input);

	if (lines == NULL) {
		exit(-1);
	}

	for (int i = 0; lines[i] != NULL; i++) {
		printf("%s", lines[i]);
	}

	exit(0);
}
