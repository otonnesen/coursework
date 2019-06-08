#!/bin/bash

if [ "$1" == "-n" ]; then
	echo "Generating test input"
	inputs/make_test.py 2> inputs/log 1> inputs/test_input7
	echo "Done"
fi

if [ "$1" == "-s" ]; then
	silent=true
else
	silent=false
fi

echo "Compiling java files"
javac Nice.java
echo "Done"

echo "Fixing test input, this may take several minutes"
done=false
while [ "$done" = false ]; do
	tmp=$((cat inputs/test_input7 | java Nice 1> /dev/null) 2>&1)
	if [ "$tmp" == "" ]; then
		done=true
	else
		t=($tmp)
		timestep=${t[2]}
		timestep=${timestep::-1}
		if [ "$silent" == "false" ]; then
			printf "%03.1f%%\n" "$(bc -l <<< "100*$timestep/100000")"
		fi
		cmd=${t[5]}
		sed -i "/$timestep $cmd/d" ./inputs/test_input7
	fi
done
echo "Done"

rm *.class inputs/log inputs/sed* -f
