#include <stdio.h>
#include <stdlib.h>

typedef struct Node Node;
typedef struct List List;

Node* new_node(void*);
void free_node(Node*);
List* new_list();
Node* insert_after(Node*, void*);
Node* insert_before(Node*, void*);
List* clear_list(List* l);
Node* delete(Node*);
Node* push(List*, void*);
Node* pop(List*);
Node* enqueue(List*, void*);
Node* dequeue(List*, void*);
void print_list(List*);
void delete_list(List*);
List* filter(List*, int (*f)(void*));
