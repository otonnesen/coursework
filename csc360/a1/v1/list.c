#include "list.h"

struct Node {
	void *data;
	Node *prev;
	Node *next;
};

struct List {
	Node *head;
	Node *tail;
};

typedef struct D D;

struct D {
	int d;
};

D *new_D(int d) {
	D *r = (D*) malloc(sizeof *r);
	r->d = d;
	return r;
}

// Allocates new List with head and tail pointers
List *new_list() {
	List *l = (List*) malloc(sizeof *l);
	Node *head = new_node(0);
	Node *tail = new_node(0);
	head->next = tail;
	tail->prev = head;
	l->head = head;
	l->tail = tail;
	return l;
}

// Frees all Nodes in and returns l
List *clear_list(List *l) {
	for (; l->head->next != l->tail; free_node(delete(l->head->next)));
	return l;
}

// Frees a list along with all of its Nodes
void delete_list(List *l) {
	clear_list(l);
	free_node(l->head);
	free_node(l->tail);
	free(l);
}

// Allocates new Node with data field d
Node *new_node(void *d) {
	Node *n = (Node*) malloc(sizeof *n);
	n->data = d;
	n->prev = NULL;
	n->next = NULL;
	return n;
}

// Frees a Node and its data
void free_node(Node *n) {
	free(n->data);
	free(n);
}

// Allocates and inserts new node with data field
// d after Node n
Node *insert_after(Node *n, void *d) {
	Node *m = new_node(d);
	m->next = n->next;
	n->next->prev = m;
	m->prev = n;
	n->next = m;
	return m;
}

// Allocates and inserts new node with data field
// d before Node n
Node *insert_before(Node *n, void *d) {
	Node *m = new_node(d);
	m->prev = n->prev;
	m->next = n;
	n->prev->next = m;
	n->prev = m;
	return m;
}

// Deletes and returns Node n
Node *delete(Node *n) {
	n->next->prev = n->prev;
	n->prev->next = n->next;
	return n;
}

// Allocates and inserts new Node with
// data field d at front of list
Node *push(List *l, void *d) {
	Node *m = new_node(d);
	m->next = l->head->next;
	l->head->next = m;
	return l->head->next;
}

// Removes and returns Node at front
// of list
Node *pop(List *l) {
	return delete(l->head->next);
}

// Allocates and inserts new Node with
// data field d at front of list
Node *enqueue(List *l, void *d) {
	return push(l, d);
}

// Removes and returns Node at tail
// of list
Node *dequeue(List *l, void *d) {
	return delete(l->tail->prev);
}

// Prints l
void print_list(List *l) {
	printf("[ ");
	for (Node *c = l->head->next; c != l->tail; c = c->next) {
		printf("%d ", ((D*)(c->data))->d);
	}
	printf("]\n");
}

// Removes items from l for which f returns
// false when given as input
List *filter(List *l, int (*f)(void*)) {
	for (Node *c = l->head->next; c != l->tail; c = c->next) {
		if (!(*f)(c->data)) {
			c = c->prev; // Don't skip next node
			free_node(delete(c->next));
		}
	}
	return l;
}

// Example filter removes odd numbers
int myFilter(void *d) {
	return ((D*)d)->d % 2 == 0;
}

// Example filter removes numbers lesser than 3
// and greater than 6
int myFilter2(void *d) {
	return ((D*)d)->d > 2 && ((D*)d)->d < 7;
}

int main() {
	List *l = new_list();
	List *l2 = new_list();
	Node *c, *c2;
	int i;
	for (c = l->head, c2 = l2->head, i = 0; i < 10; i++) {
		c = insert_after(c, new_D(i));
		c2 = insert_after(c2, new_D(i));
	}
	print_list(l);
	filter(l, *myFilter);
	print_list(l);
	filter(l2, *myFilter2);
	print_list(l2);
	delete_list(l);
	delete_list(l2);
}
