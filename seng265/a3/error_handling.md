## 1. e_in00.txt
### Illegal command value
The program is exited if any of the following occur:
* A non-numerical value is given after LW
* A non-numerical value is given after LS
* A non-numerical value is given after LM
* If a value other than 'on' or 'off' is given after FT

## 2. e_in01.txt
### Illegal command
My interpretation of the specification is that any line
beginning with a period is to be considered a command.
Based on this interpretation, any line beginning with a
period followed by any sequence of characters other than
'LW', 'LS', 'LM', or 'FT' is to be considered an invalid
command, and the program will exit.

Note: the following regular expression can be used to test for
both the first and the second error case:
/^\.LS [0-9]+|\.LW [0-9]+|\.FT (off|on)|\.LM [+-]?[0-9]+$/gm

## 3. e_in02.text
### File not found
The program will exit whenever the filename parameter of the
UVroff constructor is given a filename that is not either 'stdin'
or a valid filename. You'll notice that this test file is named
'e_in02.text' as opposed to 'e_in02.txt' to represent an invalid name
(my testing script attempts to open 'e_in02.txt').

## 4. e_in03.txt
### File is a directory
The program will exit whenever the filename parameter of the
UVroff constructor is given a filename that corresponds to a
directory. You'll notice that this test file does in fact correspond
to a directory instead of a text file.

## 5. e_in05.txt
### Non UTF-8 character encountered
The program will exit if it cannot decode one or more characters given in
the input.

