#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct Node Node;
typedef struct List List;

struct Node {
	char **data;
	Node *prev;
	Node *next;
};

struct List {
	Node *head;
	Node *tail;
};

Node *new_node(char**);
void free_node(Node*);
List *new_list();
List *clear_list(List*);
Node *append(List*, char**);
Node *delete(Node*);
char **search_prefix(List*, char*);
void print_list(List*);
void delete_list(List*);
