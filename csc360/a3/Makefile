CC = gcc
CFLAGS = -std=gnu99 -g

TARGETS = bitmap disk File

OBJ = $(patsubst %,obj/%.o,$(TARGETS))

all: 1 2

obj/%.o: io/%.c | obj
	$(CC) -c -o $@ $< $(CFLAGS)

obj:
	mkdir -p $@

.PHONY: 1
.PHONY: 2

1: $(OBJ)
	$(CC) -o test1 apps/test1.c $^ $(CFLAGS)

2: $(OBJ)
	$(CC) -o test2 apps/test2.c $^ $(CFLAGS)

.PHONY: clean

clean:
	rm -rf obj/ test1 test2 test3 loremipsum.txt
