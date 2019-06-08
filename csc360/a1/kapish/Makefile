CC = gcc
CFLAGS = -std=c99 -pedantic-errors -Wall -Werror
DEPS = list.h kapish.h
OBJ = list.o kapish.o

%.o: %.c $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS) -g
kapish: $(OBJ)
	$(CC) -o $@ $^ $(CFLAGS) -lm

.PHONY: clean

clean:
	rm -f *.o
