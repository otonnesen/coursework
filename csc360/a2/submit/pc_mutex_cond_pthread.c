#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define MAX_PRODUCERS 2
#define MAX_CONSUMERS 2
#define BUF_SIZE 5
#define MAX_JOBS -1

pthread_t producers[MAX_PRODUCERS];
pthread_t consumers[MAX_CONSUMERS];
int buffer[BUF_SIZE];
int count;
int job;
int prodid;
int consid;
pthread_mutex_t mutex;
pthread_cond_t freeslot;
pthread_cond_t fullslot;

void *produce();
void *consume();

int main() {
	job = 0, count = 0;
	consid = 0; prodid = 0;
	pthread_mutex_init(&mutex, NULL);
	pthread_cond_init(&freeslot, NULL);
	pthread_cond_init(&fullslot, NULL);

	int err, prod = 0, cons = 0;
	// Make threads
	while (prod < MAX_PRODUCERS) {
		err = pthread_create(&producers[prod], NULL, produce, NULL);
		if (err) printf("Error creating thread\n");
		prod++;
	}
	while (cons < MAX_CONSUMERS) {
		err = pthread_create(&consumers[cons], NULL, consume, NULL);
		if (err) printf("Error creating thread\n");
		cons++;
	}

	while(prod > 0) pthread_join(producers[--prod], NULL);
	while(cons > 0) pthread_join(consumers[--cons], NULL);
}

void *produce() {
	int exit = 0;
	pthread_mutex_lock(&mutex);
	int id = prodid++;
	pthread_mutex_unlock(&mutex);
	printf("Producer %d started\n", id);
	for (;;) {
		pthread_mutex_lock(&mutex);
		while(count == BUF_SIZE) pthread_cond_wait(&freeslot, &mutex);
		if (job <= MAX_JOBS || MAX_JOBS <= 0) {
			buffer[++count] = job++;
		} else {
			exit = 1;
		}
		pthread_cond_signal(&fullslot);
		pthread_mutex_unlock(&mutex);
		if (exit) pthread_exit(NULL);
		printf("Producer %d produced %d\n", id, job-1);
	}
}

void *consume() {
	int p;
	pthread_mutex_lock(&mutex);
	int id = consid++;
	pthread_mutex_unlock(&mutex);
	printf("Consumer %d started\n", id);
	for (;;) {
		pthread_mutex_lock(&mutex);
		while (count == 0) {
			if (job >= MAX_JOBS && MAX_JOBS > 0 && count == 0) {
				pthread_mutex_unlock(&mutex);
				pthread_exit(NULL);
			}
			pthread_cond_wait(&fullslot, &mutex);
		}
		p = buffer[count--];
		pthread_cond_signal(&freeslot);
		pthread_mutex_unlock(&mutex);
		printf("Consumer %d consumed %d\n", id, p);
	}
}
