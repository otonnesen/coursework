/**
 * FindAllWords.java
 * @author Mike Zastre
 * 
 * For use with Assignment #3, UVic CSC 115 (Spring 2018)
 * University of Victoria.
 */ 

/* 
 * Name: Oliver Tonnesen
 * ID: V00885732
 * Date: February 28, 2018
 * Filename: FindAllWords.java
 * Details: CSC 115 Assignment 3
 */

import java.util.*;
import java.io.*;

/**
 * When run as a program, FindAllWords creates a GameBoard
 * using the first parameter as a seed, and reads the
 * word file (whose named is provided as the second parameter)
 * line-by-line. For each word, this program determines if
 * the word can be found in the GameBoard instance created.
 */
public class FindAllWords {
    public static void main(String args[]) {
        if (args.length < 2) {
            System.err.println(
                "usage: java ValidateWord <seed> <filename>"
            );
            System.exit(1);
        }

        try {
            long seed = Long.parseLong(args[0]);
            String word = args[1].toUpperCase();

            GameBoard gb = new GameBoard(seed);
            System.out.println(gb.toString());

            /* Student code here for opening the file,
             * then reading it line by line, and for each
             * line (which is a single word) determining
             * whether or not the word appears on
             * the GameBoard instance.
             *
             * Note: The name of the file is in argv[1].
             */

	    try {
		    Scanner in = new Scanner(new File(args[1]));
		    String t;
		    while(in.hasNext()) {
			    t = in.nextLine();
			    if(gb.isWord(t) != null) {
				    System.out.print(t+": ");
				    System.out.println(gb.isWord(t));
			    }
		    }

	    } catch(FileNotFoundException e) {
		    System.out.println("No such file");
		    System.exit(-1);
	    }


        }
        catch (NumberFormatException nfe) {
            System.err.println(
                "'" + args[0] + "' " + 
                "given for <seed>; <seed> needs to be a valid integer"
            );
            System.exit(1);
        }
    }
}
