#!/bin/bash

mkdir tmp
gcc -std=c99 uvroff.c -o uvroff


for i in 01 02 03 04 05 06 07 08 09 10; do
	./uvroff tests/in$i.txt > tmp/test$i;
done

for i in 01 02 03 04 05 06 07 08 09 10; do
	DIFF=$(diff tmp/test$i tests/out$i.txt);
	if [ "$DIFF" != "" ]; then
		echo "test $i FAILED"
	else
		echo "test $i PASSED"
	fi
done

rm tmp -r uvroff
