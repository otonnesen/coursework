#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <pthread.h>
#include <unistd.h>

#define NUM_ITERATIONS 1000

#ifdef VERBOSE
#define VERBOSE_PRINT(S, ...) printf (S, ##__VA_ARGS__);
#else
#define VERBOSE_PRINT(S, ...) ;
#endif

struct Agent {
	pthread_mutex_t mutex;
	pthread_cond_t  match;
	pthread_cond_t  paper;
	pthread_cond_t  tobacco;
	pthread_cond_t  smoke;
};

struct Agent* createAgent() {
	struct Agent* agent = malloc (sizeof (struct Agent));
	pthread_mutex_init(&agent->mutex, NULL);
	pthread_cond_init (&agent->paper, NULL);
	pthread_cond_init (&agent->match, NULL);
	pthread_cond_init (&agent->tobacco, NULL);
	pthread_cond_init (&agent->smoke, NULL);
	return agent;
}

int match_a = 0, paper_a = 0, tobacco_a = 0;
pthread_cond_t match_c;
pthread_cond_t paper_c;
pthread_cond_t tobacco_c;

int threads_ready = 0;
pthread_cond_t thread_ready;

/**
* You might find these declarations helpful.
*   Note that Resource enum had values 1, 2 and 4 so you can combine resources;
*   e.g., having a MATCH and PAPER is the value MATCH | PAPER == 1 | 2 == 3
*/
enum Resource            {    MATCH = 1, PAPER = 2,   TOBACCO = 4};
char* resource_name [] = {"", "match",   "paper", "", "tobacco"};

int signal_count [5];  // # of times resource signalled
int smoke_count  [5];  // # of times smoker with resource smoked

/**
* This is the agent procedure.  It is complete and you shouldn't change it in
* any material way.  You can re-write it if you like, but be sure that all it does
* is choose 2 random reasources, signal their condition variables, and then wait
* wait for a smoker to smoke.
*/
void* agent (void* av) {
	VERBOSE_PRINT ("Agent started\n");
	struct Agent* a = av;
	static const int choices[]         = {MATCH|PAPER, MATCH|TOBACCO, PAPER|TOBACCO};
	static const int matching_smoker[] = {TOBACCO,     PAPER,         MATCH};

	pthread_mutex_lock (&a->mutex);
	for (int i = 0; i < NUM_ITERATIONS; i++) {
		int r = random() % 3;
		signal_count [matching_smoker [r]] ++;
		int c = choices [r];
		if (c & MATCH) {
			VERBOSE_PRINT ("match available\n");
			pthread_cond_signal (&a->match);
		}
		if (c & PAPER) {
			VERBOSE_PRINT ("paper available\n");
			pthread_cond_signal (&a->paper);
		}
		if (c & TOBACCO) {
			VERBOSE_PRINT ("tobacco available\n");
			pthread_cond_signal (&a->tobacco);
		}
		VERBOSE_PRINT ("agent is waiting for smoker to smoke\n");
		pthread_cond_wait (&a->smoke, &a->mutex);
	}
	pthread_mutex_unlock (&a->mutex);
	return NULL;
}

void *match_sig(void *a) {
	struct Agent *agent = a;
	pthread_mutex_lock(&agent->mutex);
	threads_ready++;
	pthread_cond_signal(&thread_ready);
	for (;;) {
		VERBOSE_PRINT ("match_sig waiting\n");
		pthread_cond_wait(&agent->match, &agent->mutex);
		if (paper_a) {
			paper_a = 0;
			pthread_cond_signal(&tobacco_c);
		} else if (tobacco_a) {
			tobacco_a = 0;
			pthread_cond_signal(&paper_c);
		} else {
			match_a = 1;
		}
	}
}

void *paper_sig(void *a) {
	struct Agent *agent = a;
	pthread_mutex_lock(&agent->mutex);
	threads_ready++;
	pthread_cond_signal(&thread_ready);
	for (;;) {
		VERBOSE_PRINT ("paper_sig waiting\n");
		pthread_cond_wait(&agent->paper, &agent->mutex);
		if (match_a) {
			match_a = 0;
			pthread_cond_signal(&tobacco_c);
		} else if (tobacco_a) {
			tobacco_a = 0;
			pthread_cond_signal(&match_c);
		} else {
			paper_a = 1;
		}
	}
}

void *tobacco_sig(void *a) {
	struct Agent *agent = a;
	pthread_mutex_lock(&agent->mutex);
	threads_ready++;
	pthread_cond_signal(&thread_ready);
	for (;;) {
		VERBOSE_PRINT ("tobacco_sig waiting\n");
		pthread_cond_wait(&agent->tobacco, &agent->mutex);
		if (match_a) {
			match_a = 0;
			pthread_cond_signal(&paper_c);
		} else if (paper_a) {
			paper_a = 0;
			pthread_cond_signal(&match_c);
		} else {
			tobacco_a = 1;
		}
	}
}

void *match(void *a) {
	struct Agent *agent = a;
	pthread_mutex_lock(&agent->mutex);
	threads_ready++;
	pthread_cond_signal(&thread_ready);
	for (;;) {
		VERBOSE_PRINT ("match waiting\n");
		pthread_cond_wait(&match_c, &agent->mutex);
		pthread_cond_signal(&agent->smoke);
		smoke_count[MATCH]++;
	}
}

void *paper(void *a) {
	struct Agent *agent = a;
	pthread_mutex_lock(&agent->mutex);
	threads_ready++;
	pthread_cond_signal(&thread_ready);
	for (;;) {
		VERBOSE_PRINT ("paper waiting\n");
		pthread_cond_wait(&paper_c, &agent->mutex);
		pthread_cond_signal(&agent->smoke);
		smoke_count[PAPER]++;
	}
}

void *tobacco(void *a) {
	struct Agent *agent = a;
	pthread_mutex_lock(&agent->mutex);
	threads_ready++;
	pthread_cond_signal(&thread_ready);
	for (;;) {
		VERBOSE_PRINT ("tobacco waiting\n");
		pthread_cond_wait(&tobacco_c, &agent->mutex);
		pthread_cond_signal(&agent->smoke);
		smoke_count[TOBACCO]++;
	}
}

int main (int argc, char** argv) {
	pthread_t tid[7];
	struct Agent*  a = createAgent();

	pthread_cond_init(&match_c, NULL);
	pthread_cond_init(&paper_c, NULL);
	pthread_cond_init(&tobacco_c, NULL);

	pthread_cond_init(&thread_ready, NULL);

	pthread_create(&tid[0], NULL, match, a);
	pthread_create(&tid[1], NULL, paper, a);
	pthread_create(&tid[2], NULL, tobacco, a);
	pthread_create(&tid[3], NULL, match_sig, a);
	pthread_create(&tid[4], NULL, paper_sig, a);
	pthread_create(&tid[5], NULL, tobacco_sig, a);

	// Ensure all smoker threads are ready before starting agent thread.
	pthread_mutex_lock(&a->mutex);
	while (threads_ready != 6) pthread_cond_wait(&thread_ready, &a->mutex);
	pthread_mutex_unlock(&a->mutex);
	pthread_cond_destroy(&thread_ready);

	pthread_create(&tid[6], NULL, agent, a);
	pthread_join(tid[6], NULL);
	assert (signal_count [MATCH]   == smoke_count [MATCH]);
	assert (signal_count [PAPER]   == smoke_count [PAPER]);
	assert (signal_count [TOBACCO] == smoke_count [TOBACCO]);
	assert (smoke_count [MATCH] + smoke_count [PAPER] + smoke_count [TOBACCO] == NUM_ITERATIONS);
	printf ("Smoke counts: %d matches, %d paper, %d tobacco\n",
	smoke_count [MATCH], smoke_count [PAPER], smoke_count [TOBACCO]);
}
