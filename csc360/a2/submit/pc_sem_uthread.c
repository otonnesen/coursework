#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include "uthread.h"
#include "uthread_sem.h"

#define MAX_ITEMS 10
const int NUM_ITERATIONS = 200;
const int NUM_CONSUMERS  = 2;
const int NUM_PRODUCERS  = 2;

int histogram [MAX_ITEMS+1]; // histogram [i] == # of times list stored i items

int items = 0;

uthread_sem_t mutex;
uthread_sem_t empty;
uthread_sem_t full;

void* producer (void* v) {
	for (int i=0; i<NUM_ITERATIONS; i++) {
		uthread_sem_wait(empty);
		uthread_sem_wait(mutex);
		items++;
		assert (0 <= items && items <= MAX_ITEMS);
		histogram[items]++;
		uthread_sem_signal(mutex);
		uthread_sem_signal(full);
	}
	return NULL;
}

void* consumer (void* v) {
	for (int i=0; i<NUM_ITERATIONS; i++) {
		uthread_sem_wait(full);
		uthread_sem_wait(mutex);
		items--;
		assert (0 <= items && items <= MAX_ITEMS);
		histogram[items]++;
		uthread_sem_signal(mutex);
		uthread_sem_signal(empty);
	}
	return NULL;
}

int main (int argc, char** argv) {
	uthread_t t[4];

	uthread_init (4);
	mutex = uthread_sem_create(1);
	empty = uthread_sem_create(MAX_ITEMS);
	full = uthread_sem_create(0);

	int j;
	for (j = 0; j < NUM_PRODUCERS; j++) {
		t[j] = uthread_create(producer, NULL);
	}
	for (; j < NUM_CONSUMERS+NUM_PRODUCERS; j++) {
		t[j] = uthread_create(consumer, NULL);
	}

	while (j > 0) uthread_join(t[--j], NULL);

	printf ("items value histogram:\n");
	int sum=0;
	for (int i = 0; i <= MAX_ITEMS; i++) {
		printf ("  items=%d, %d times\n", i, histogram [i]);
		sum += histogram [i];
	}
	assert (sum == sizeof (t) / sizeof (uthread_t) * NUM_ITERATIONS);
}
