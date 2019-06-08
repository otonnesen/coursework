/*
 * UVic SENG 265, Summer 2018,  A#4
 *
 * This will contain the bulk of the work for the fourth assignment. It
 * provide similar functionality to the class written in Python for
 * assignment #3.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "formatter.h"

#define DEFAULT_BUFLEN 80

char** readlines(FILE*);
char** format(char**);

typedef struct Commands {
	int FT;
	int LW;
	int LM;
	int LS;
} Commands;
void parse_cmd(Commands*, char*);

char **format_file(FILE *infile) {
	char** l = readlines(infile);
	return format(l);
}

typedef struct Buffer {
	char* text;
	int cursor;
	int size;
} Buffer;

typedef struct Lines {
	char** lines;
	int cursor;
	int size;
} Lines;

Buffer* write(Buffer*, char*);
Buffer* reset_buffer(Buffer*);
Buffer* new_buffer();
void print_buffer(Buffer*);
Lines* new_lines();
Lines* add_buffer(Lines*, Buffer*);

char **format_lines() {
	char** l = readlines(stdin);
	return format(l);
}

char** format(char** lines) {

	Commands cmd = {0, 0, 0, 0};

	Buffer* buf = new_buffer();
	int linecount = 0;
	int ewn = 0;

	Lines* out = new_lines();

	for (int i = 0; lines[i] != NULL; i++) {
		if (lines[i][0] == '.') {
			/* Command line */
			parse_cmd(&cmd, lines[i]);
		} else if (cmd.FT == 0 || cmd.LW == 0) {
			/* No formatting */
			write(buf, lines[i]);
			write(buf, "\n");
			add_buffer(out, buf);
			buf = reset_buffer(buf);
			ewn = 1;
		} else if (strcmp(lines[i], "") == 0) {
			/* Empty line */
			if (ewn) {
				for (int j = 0; j < 2*cmd.LS+1; j++) {
					write(buf, "\n");
				}
			} else {
				for (int j = 0; j < 2*cmd.LS; j++) {
					write(buf, "\n");
				}
				write(buf, "\n\n");
			}
			add_buffer(out, buf);
			buf = reset_buffer(buf);
			linecount = 0;
			ewn = 1;
		} else {
			/* Formatting on */
			for (char* word = strtok(lines[i], " "); word != NULL; word = strtok(NULL, " \n")) {
				if (linecount == 0) {
					for (int j = 0; j < cmd.LM; j++) {
						write(buf, " ");
					}
					write(buf, word);
					linecount = cmd.LM + strlen(word);
					ewn = 0;
				} else if (linecount + strlen(word) + 1 <= cmd.LW) {
					write(buf, " ");
					write(buf, word);
					linecount = linecount + 1 + strlen(word);
					ewn = 0;
				} else {
					for (int j = 0; j < cmd.LS; j++) {
						write(buf, "\n");
					}
					write(buf, "\n");
					add_buffer(out, buf);
					buf = reset_buffer(buf);

					for (int j = 0; j < cmd.LM; j++) {
						write(buf, " ");
					}
					write(buf, word);
					linecount = cmd.LM + strlen(word);
					ewn = 0;
				}
			}
		}
	}

	if (!ewn) {
		write(buf, "\n");
	}
	add_buffer(out, buf);

	free(buf->text);
	free(buf);
	for (int i = 0; lines[i] != NULL; i++) {
		free(lines[i]);
	}
	free(lines);

	return out->lines;
}

Buffer* new_buffer() {
	Buffer* buf = (Buffer*) malloc(sizeof *buf);
	buf->text = NULL;
	buf->cursor = 0;
	buf->size = DEFAULT_BUFLEN;
	return buf;
}

void print_buffer(Buffer* buf) {
	if (buf->text == NULL) return;
	printf("%s", buf->text);
}

Lines* new_lines() {
	Lines* l = (Lines*) malloc(sizeof *l);
	l->lines = NULL;
	l->cursor = 0;
	l->size = DEFAULT_BUFLEN;
	return l;
}

Lines* add_buffer(Lines* out, Buffer* buf) {
	if (buf->text == NULL) return out;
	if (out->lines == NULL) {
		out->lines = (char**) malloc(out->size * sizeof(char*));
		out->lines[out->cursor] = (char*) malloc((strlen(buf->text)+1)*sizeof(char));
		strncpy(out->lines[out->cursor], buf->text, strlen(buf->text));
		out->lines[out->cursor][strlen(buf->text)] = '\0';
		out->cursor++;
		out->lines[out->cursor] = NULL;
	} else {
		if (out->cursor+1 >= out->size) {
			out->size = out->size * 2;
			out->lines = (char**) realloc(out->lines, out->size*sizeof(char*));
		}
		out->lines[out->cursor] = (char*) malloc((strlen(buf->text)+1)*sizeof(char));
		strncpy(out->lines[out->cursor], buf->text, strlen(buf->text));
		out->lines[out->cursor][strlen(buf->text)] = '\0';
		out->cursor++;
		out->lines[out->cursor] = NULL;
	}
	return out;
}

Buffer* write(Buffer* buf, char* text) {
	if (buf->text == NULL) {
		buf->text = (char*) malloc(buf->size * sizeof(char));
		strncpy(buf->text, text, strlen(text));
		buf->text[strlen(text)] = '\0';
		buf->cursor = strlen(text);
	} else {
		if (strlen(text)+buf->cursor >= buf->size) {
			buf->size = buf->size * 2;
			buf->text = (char*) realloc(buf->text, buf->size*sizeof(char));
		}
		strcat(buf->text, text);
		buf->cursor = buf->cursor + strlen(text);
		buf->text[buf->cursor] = '\0';
	}
	return buf;
}

Buffer* reset_buffer(Buffer* buf) {
	free(buf->text);
	free(buf);
	buf = new_buffer();
	return buf;
}

void parse_cmd(Commands* cmd, char* line) {
	char* tok = strtok(line, " ");
	if (strcmp(tok, ".FT") == 0) {
		tok = strtok(NULL, " ");
		if (strcmp(tok, "on") == 0) {
			cmd->FT = 1;
		} else {
			cmd->FT = 0;
		}
	} else if (strcmp(tok, ".LW") == 0) {
		cmd->LW = atoi(strtok(NULL, " "));
		cmd->FT = 1;
	} else if (strcmp(tok, ".LM") == 0) {
		tok = strtok(NULL, " ");
		if (tok[0] == '+') {
			cmd->LM = cmd->LM + atoi(tok+1);
		} else if (tok[0] == '-') {
			cmd->LM = cmd->LM - atoi(tok+1);
		} else {
			cmd->LM = atoi(tok);
		}

		if (cmd->LM > cmd->LW - 20) {
			cmd->LM = cmd->LW - 20;
		}
		if (cmd->LM < 0) {
			cmd->LM = 0;
		}
	} else if (strcmp(tok, ".LS") == 0) {
		cmd->LS = atoi(strtok(NULL, " "));
	} else {
		exit(-1);
	}
}

char** readlines(FILE* in) {

	int buf_size = DEFAULT_BUFLEN;
	int lines_size = DEFAULT_BUFLEN;
	char* buf = (char*) malloc(buf_size*sizeof(char));
	char** lines = (char**) malloc(lines_size*sizeof(char*));
	int buf_inc = 0;
	int lines_inc = 0;
	char c;

	while((c = fgetc(in)) != EOF) {
		if (c == '\n') {
			if (buf_inc == buf_size) {
				buf_size = buf_size * 2;
				buf = (char*) realloc(buf, buf_size*sizeof(char));
			}
			buf[buf_inc++] = '\0';
			if (lines_inc == lines_size) {
				lines_size = lines_size * 2;
				lines = (char**) realloc(lines, lines_size*sizeof(char*));
			}
			lines[lines_inc] = (char*) malloc(buf_inc*sizeof(char));
			strncpy(lines[lines_inc++], buf, buf_inc);
			buf_inc = 0;
			buf_size = DEFAULT_BUFLEN;
			free(buf);
			buf = (char*) malloc(buf_size*sizeof(char));
			continue;
		}

		if (buf_inc == buf_size) {
			buf_size = buf_size * 2;
			buf = (char*) realloc(buf, buf_size*sizeof(char));
		}
		buf[buf_inc++] = c;
	}
	free(buf);
	lines = realloc(lines, (lines_inc+1)*sizeof(char*));
	lines[lines_inc] = NULL;
	return lines;
}
