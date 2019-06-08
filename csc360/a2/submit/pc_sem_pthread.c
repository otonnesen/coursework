#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define MAX_PRODUCERS 2
#define MAX_CONSUMERS 10
#define BUF_SIZE 20
#define MAX_JOBS -1

pthread_t producers[MAX_PRODUCERS];
pthread_t consumers[MAX_CONSUMERS];
int buffer[BUF_SIZE];
int count;
int job;
int prodid;
int consid;
sem_t mutex;
sem_t empty;
sem_t full;

void *produce();
void *consume();

int main() {
	job = 0, count = 0;
	consid = 0; prodid = 0;
	sem_init(&mutex, 0, 1);
	sem_init(&empty, 0, BUF_SIZE);
	sem_init(&full, 0, 0);

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

	/* while(prod > 0) if (!pthread_join(producers[--prod], NULL)) printf("Joined with prod thread %d\n", prod); */
	/* while(cons > 0) if (!pthread_join(consumers[--cons], NULL)) printf("Joined with cons thread %d\n", cons); */
	while(prod > 0) pthread_join(producers[--prod], NULL);
	while(cons > 0) pthread_join(consumers[--cons], NULL);
}

void *produce() {
	int exit = 0;
	sem_wait(&mutex);
	int id = prodid++;
	sem_post(&mutex);
	for (;;) {
		sem_wait(&empty);
		sem_wait(&mutex);
		if (job <= MAX_JOBS || MAX_JOBS <= 0) {
			buffer[++count] = job++;
		} else {
			exit = 1;
		}
		sem_post(&mutex);
		sem_post(&full);
		if (exit) pthread_exit(NULL);
		printf("Producer %d produced %d\n", id, job-1);
		// usleep(100000); // Uncomment this (along with line 67) to see consumers working slower than producers
	}
}

void *consume() {
	int p;
	sem_wait(&mutex);
	int id = consid++;
	sem_post(&mutex);
	for (;;) {
		if (job >= MAX_JOBS && MAX_JOBS > 0 && count == 0) pthread_exit(NULL);
		if (!sem_trywait(&full)) { // Spin to avoid deadlock when producers stop
			sem_wait(&mutex);
			p = buffer[count--];
			sem_post(&mutex);
			sem_post(&empty);
			printf("Consumer %d consumed %d\n", id, p);
		}
		// usleep(1000000); // Uncomment this (along with line 55) to see consumers working slower than producers
	}
}
