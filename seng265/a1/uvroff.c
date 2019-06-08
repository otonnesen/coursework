#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINE_LEN 132
#define MAX_LINES 500

void LW(FILE*, int);

int main(int argc, char* argv[]) {

	FILE *in = fopen(argv[1], "r");
	if (in == NULL) {
		printf("No such file '%s'.\n", argv[1]);
		return -1;
	}

	char *delimiters = " \n";

	char buffer[MAX_LINE_LEN];
	char rawline[MAX_LINE_LEN]; /* Used to print raw (non-tokenized) input after .FT is determined to be 'off' */
	char *word;
	int ends_with_newline; /* Determines need to append extra '\n' */
	int linecount = 0; /* Counts chars in line */
	/* Command booleans */
	int LW = 0;
	int LM = 0;
	int LS = 0;
	int FT = 0;

	while (fgets(buffer, MAX_LINE_LEN, in)) {
		strncpy(rawline, buffer, MAX_LINE_LEN);
		word = strtok(buffer, delimiters);

		/* Inserts newlines if the line is empty */
		if (word == NULL) {
			ends_with_newline = 1;
			for (int i = 0; i < LS; i++) printf("\n\n");
			printf("\n\n");
			linecount = 0;
			continue; /* Avoid re-printing the newline */
		}
		ends_with_newline = 0;

		/* Checking for command keywords */
		if (*word == '.') {
			if (strcmp(word, ".LW") == 0) {
				LW = atoi(strtok(NULL, delimiters));
				FT = 1; /* LW implies FT, as per the spec */
			} else if (strcmp(word, ".FT") == 0) {
				word = strtok(NULL, delimiters);
				if (strcmp(word, "on") == 0) {
					FT = 1;
				} else {
					FT = 0;
				}
			} else if (strcmp(word, ".LM") == 0) {
				LM = atoi(strtok(NULL, delimiters));
			} else if (strcmp(word, ".LS") == 0) {
				LS = atoi(strtok(NULL, delimiters));
			}
			continue; /* Avoid re-printing the line of text */
		}

		if (LW && FT) { /* If LW is not enabled, neither are LM or LS */
			while (word) {
				if (linecount == 0) { /* No logic required to check size of token */
					for (int i = 0; i < LM; i++) printf(" "); /* LM spaces are prepended to the new line */
					printf("%s", word);
					linecount = strlen(word);
				} else {
					if (linecount + strlen(word) + 1 <= LW-LM) { /* Word (with a space) fits on the current line */
						printf(" %s", word);
						linecount += strlen(word) + 1;
					} else { /* Word will not fit on the current line, so one newline is added, plus */
						for (int i = 0; i < LS+1; i++) printf("\n"); /* LS extra newlines */
						for (int i = 0; i < LM; i++) printf(" "); /* LM spaces are prepended to the new line */
						printf("%s", word);
						linecount = strlen(word);
					}
				}
				word = strtok(NULL, delimiters);
			}
		} else { /* If not LW and FT, print raw text */
			printf("%s", rawline);
		}
	}

	if (!ends_with_newline && FT) { /* newline appended if needed */
		printf("\n");
	}
}
