#include "list.h"

// Allocates new List with head and tail pointers
List *new_list() {
	List *l = (List*) malloc(sizeof *l);
	Node *head = new_node(NULL);
	Node *tail = new_node(NULL);
	head->next = tail;
	tail->prev = head;
	l->head = head;
	l->tail = tail;
	return l;
}

// Frees all Nodes in and returns l
List *clear_list(List *l) {
	while (l->head->next != l->tail) if (l->head->next) free_node(delete(l->head->next));
	return l;
}

// Frees a Node and its data
void free_node(Node *n) {
	if (!n) return;
	if (!n->data) {
		free(n);
		return;
	}
	for (int i = 0; n->data[i] != NULL; i++)
		if(n->data[i])
			free(n->data[i]);
	free(n->data);
	free(n);
}

// Frees a list along with all of its Nodes
void delete_list(List *l) {
	clear_list(l);
	free(l->head);
	free(l->tail);
	free(l);
}

char **cpy_toks(char **d) {
	int buf_size;
	int toks_size = 1;
	char *buf;
	char **toks = (char**) malloc(toks_size * sizeof(char*));

	int i;
	for (i = 0; d[i] != NULL; i++) {
		if (i >= toks_size) {
			toks_size *= 2;
			toks = (char**) realloc(toks, toks_size * sizeof(char*));
		}
		buf_size = 1;
		buf = (char*) malloc(buf_size * sizeof(char));
		int j;
		for (j = 0; d[i][j] != '\0'; j++) {
			if (j >= buf_size) {
				buf_size *= 2;
				buf = (char*) realloc(buf, buf_size * sizeof(char));
			}
			buf[j] = d[i][j];
		}
		buf = (char*) realloc(buf, (j+1) * sizeof(char));
		buf[j] = '\0';
		toks[i] = buf;
	}
	toks = (char**) realloc(toks, (i+1) * sizeof(char*));
	toks[i] = NULL;
	return toks;
}

// Allocates new Node with data field d
Node *new_node(char **d) {
	Node *n = (Node*) malloc(sizeof *n);
	n->prev = NULL;
	n->next = NULL;
	if (d == NULL) return n;
	n->data = cpy_toks(d);
	return n;
}

// Deletes and returns Node n
Node *delete(Node *n) {
	n->next->prev = n->prev;
	n->prev->next = n->next;
	return n;
}

Node *append(List *l, char **d) {
	Node *m = new_node(d);
	m->next = l->tail;
	m->prev = l->tail->prev;
	m->prev->next = m;
	l->tail->prev = m;
	return m;
}

char **search_prefix(List *l, char *prefix) {
	for (Node *c = l->tail->prev; c != l->head; c = c->prev) {
		if (!strncmp(c->data[0], prefix, strlen(prefix))) {
			return cpy_toks(c->data);
		}
	}
	return NULL;
}

// Prints l
void print_list(List *l) {
	int k = 0;
	for (Node *c = l->head->next; c != l->tail; c = c->next, k++) {
		printf("%d: ", k);
		for (int i = 0; c->data[i]; i++) {
			printf("%s ", c->data[i]);
		}
		printf("\n");
	}
}
