CC=gcc
# CFLAGS=-c -g -Wall -DDEBUG
CFLAGS=-c -g -Wall -DDEBUG -std=c99
LDFLAGS=

all: uvroff2 driver

uvroff2: uvroff2.o formatter.o
	$(CC) $(LDFLAGS) uvroff2.o formatter.o -o uvroff2

driver: driver.o formatter.o
	$(CC) $(LDFLAGS) driver.o formatter.o -o driver

driver.o: driver.c formatter.h
	$(CC) $(CFLAGS) driver.c

uvroff2.o: uvroff2.c formatter.h
	$(CC) $(CFLAGS) uvroff2.c

formatter.o: formatter.c formatter.h
	$(CC) $(CFLAGS) formatter.c

clean:
	/bin/rm -f uvroff2 driver uvroff2.o driver.o formatter.o
