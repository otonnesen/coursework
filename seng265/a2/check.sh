#!/bin/bash

RED='\033[0;31m'
GREEN='\33[0;32m'
NC='\33[0m'

if [ "$1" == "-v" ]; then
	vimdiff <(./uvroff.py tests/in$2.txt) tests/out$2.txt
	exit
fi

if [ "$1" != "" ]; then
	DIFF=$(diff <(./uvroff.py tests/in$1.txt) tests/out$1.txt)
	if [ "$DIFF" != "" ]; then
		printf "${RED}FAILED${NC}\n"
	else
		printf "${GREEN}PASSED${NC}\n"
	fi
	exit
fi

for ((i=0;i<=20;i++)); do
	if [ $i -lt 10 ]; then
		DIFF=$(diff <(./uvroff.py tests/in0$i.txt) tests/out0$i.txt);
		if [ "$DIFF" != "" ]; then
			printf "test 0${i} ${RED}FAILED${NC}\n"
		else
			printf "test 0${i} ${GREEN}PASSED${NC}\n"
		fi
	else
		DIFF=$(diff <(./uvroff.py tests/in$i.txt) tests/out$i.txt);
		if [ "$DIFF" != "" ]; then
			printf "test ${i} ${RED}FAILED${NC}\n"
		else
			printf "test ${i} ${GREEN}PASSED${NC}\n"
		fi
	fi
done
