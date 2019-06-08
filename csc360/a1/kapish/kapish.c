#include "kapish.h"
#include "list.h"

List *history;

int main(int argc, char **argv) {
	signal(SIGINT, SIG_IGN);
	char *userhome = getenv("HOME");
	char *krc_path = (char*) malloc((strlen(userhome)+strlen("/.kapishrc"))*sizeof(char));
	strncat(krc_path, userhome, strlen(userhome));
	strncat(krc_path, "/.kapishrc", strlen("/.kapishrc"));
	if (access(krc_path, R_OK) != -1) {
		FILE *krc = fopen(krc_path, "r");
		loop(krc); // Read rc file if it exists
		fclose(krc);
	}
	history = new_list();
	loop(stdin);
	free(krc_path);
	return 0;
}

void loop(FILE *stream) {
	char *line;
	char **tokens;
	int stop;
	do {
		line = read_line(stream);
		if (line != NULL) if (line[0] == -1 && stream == stdin) printf("\n");
		tokens = parse_kapish(line);
		if (tokens != NULL && stream == stdin) append(history, tokens);
		stop = execute_kapish(tokens);
		if (line) free(line);
		if (tokens) free(tokens);
	} while (!stop);
	if (history) delete_list(history);
}

// Reads and returns a line of user input.
char *read_line(FILE *stream) {
	char *buf = (char*) malloc((MAX_BUF_BYTES+1) * sizeof(char));
	if (!buf) {
		fprintf(stderr, "%s: Error allocating. Aborting...", SHELL);
		exit(-1);
	}
	char c;
	int pos = 0;
	char cwd[1024];
	if (stream == stdin) printf("%s? ", getenv("FANCYPROMPT") ? getcwd(cwd, sizeof(cwd)) : ""); // TODO: Parse PS1 env var instead of just printing cwd
	for (;;) {
		c = getc(stream);
		if (c == '\n' || c == EOF) {
			if (pos == 0 && c == EOF) {
				buf[0] = c;
				buf[1] = '\0';
				return buf;
			}
			if (pos >= MAX_BUF_BYTES+1) {
				fprintf(stderr, "%s: Maximum input length (%d) exceeded.\n", SHELL, MAX_BUF_BYTES);
				free(buf);
				return NULL;
			} else {
				buf[pos++] = '\0';
				return buf;
			}
		} else {
			if (pos >= MAX_BUF_BYTES+1) {
				fprintf(stderr, "%s: Maximum input length (%d) exceeded.\n", SHELL, MAX_BUF_BYTES);
				while (fgetc(stream) != '\n');
				free(buf);
				return NULL;
			} else {
				buf[pos++] = c;
			}
		}
	}
}

// Separates a single space-delimited string into
// an array of tokens.
char **parse_kapish(char *line) {
	if (line == NULL) return NULL;
	char *cpy = (char*) malloc(strlen(line)*sizeof(char));
	strncpy(cpy, line, strlen(line));
	char **tokens;
	int size = 1;
	int pos = 0;
	int skip = 0;
	if (line[0] == -1) {
		tokens = (char**) malloc(2*sizeof(char*));
		tokens[0] = "exit";
		tokens[1] = NULL;
		return tokens;
	}
	if (line[0] == '!') {
		if (!line[1]) return NULL;
		tokens = search_prefix(history, strtok(cpy+1, " "));
		if (!tokens) {
			printf("%s: %s: event not found\n", SHELL, strtok(cpy, " "));
			free(cpy);
			return NULL;
		}
		for (int i = 0; tokens[i]; i++) pos++;
		size = pos;
		skip = 1;
	}
	if (pos == 0) tokens = (char**) malloc(size * sizeof(char*));
	for (char *tok = strtok(line, " "); tok; tok = strtok(NULL, " ")) {
		if (skip) {
			skip = 0;
			continue;
		}
		if (pos == size) {
			size *= 2;
			tokens = (char**) realloc(tokens, size*sizeof(char*));
		}
		tokens[pos++] = tok;
	}
	tokens = (char**) realloc(tokens, (pos+1)*sizeof(char*));
	tokens[pos] = NULL; // Terminal NULL communicates when to end
	return tokens;
}

// Executes built-in kapish command.
int execute_kapish(char **tokens) {
	if (tokens == NULL) return 0;
	if (tokens[0] == NULL) return 0;
	if (!strncmp(tokens[0], "setenv", strlen("setenv"))) {
		if (tokens[1] == NULL) {
			perror(SHELL);
			return 0;
		}
		if (setenv(tokens[1], tokens[2] == NULL ? "" : tokens[2], 1)) {
			perror(SHELL);
		}
	} else if (!strncmp(tokens[0], "unsetenv", strlen("unsetenv"))) {
		if (tokens[1] == NULL) {
			fprintf(stderr, "No variable given.");
			return 0;
		}
		if (unsetenv(tokens[1])) {
			perror(SHELL);
		}
	} else if (!strncmp(tokens[0], "cd", strlen("cd"))) {
		if (tokens[1] == NULL) {
			if (chdir(getenv("HOME"))) {
				perror(SHELL);
			}
			return 0;
		}
		if (chdir(tokens[1])) {
			perror(SHELL);
		}
	} else if (!strncmp(tokens[0], "exit", strlen("exit"))) {
		return 1;
	} else if (!strncmp(tokens[0], "getenv", strlen("getenv"))) {
		if (tokens[1] == NULL) {
			fprintf(stderr, "No variable given.");
			return 0;
		}
		char *c = getenv(tokens[1]);
		printf("%s\n", c == NULL ? "" : c);
	} else if (!strncmp(tokens[0], "history", strlen("history"))) {
		print_list(history);
	} else {
		int pid = fork();
		if (pid == 0) {
			if (execvp(tokens[0], tokens)) {
				perror(SHELL);
			}
			exit(-1);
		} else {
			wait(NULL);
		}
	}
	return 0;
}
