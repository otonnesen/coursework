#!/bin/bash

javac Nice.java

if [ "$1" == "-v" ]; then
	vimdiff <(cat tests/in$2.txt | java Nice) tests/out$2.txt
	exit
fi

for ((i=1;i<6;i++)); do
	DIFF=$(diff <(cat tests/in$i.txt | java Nice) tests/out$i.txt)
	if [ "$DIFF" != "" ]; then
		echo "FAILED"
	else
		echo "PASSED"
	fi
done

rm *.class
