# Test files for Assignment #2 (SENG 265, Summer 2018)

There are twenty pairs of files. For example, the first pair consists of:

* in05.txt: contains text and formatting commands for fifth test
* out05.txt: expected output to be produced when in05.txt is the input

One suggested test workflow is based on taking the output from the
uvroffprogram and piping it into diff. Diff normally takes two input
files are arguments, but you can specify STDIN as one of the arguments
by using the hyphen.  For example, below is one way to perform the first
two tests for correctness:

----
BIN=/path/to/your/executable/uvroff
TESTS=/path/to/the/directory/with/tests

$BIN $TESTS/in01.txt | diff $TESTS/out01.txt -
$BIN $TESTS/in02.txt | diff $TESTS/out02.txt -
----

When diff produces no output, that means its two input text streams
(file & file, or file & stdin) are identical. Writing a bash shell
script to do these diffs for all ten tests is left as an exercise to
the reader.
