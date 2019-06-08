/* Ladder.java
   CSC 225 - Summer 2018

   Starter code for Programming Assignment 3

   B. Bird - 06/30/2018
*/

import java.io.*;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Ladder{


	public static void showUsage(){
		System.err.printf("Usage: java Ladder <word list file> <start word> <end word>\n");
	}


	public static void main(String[] args){

		//At least four arguments are needed
		if (args.length < 3){
			showUsage();
			return;
		}
		String wordListFile = args[0];
		String startWord = args[1].trim();
		String endWord = args[2].trim();


		//Read the contents of the word list file into a LinkedList (requires O(nk) time for
		//a list of n words whose maximum length is k).
		//(Feel free to use a different data structure)
		BufferedReader br = null;
		LinkedList<String> words = new LinkedList<String>();

		try{
			br = new BufferedReader(new FileReader(wordListFile));
		}catch( IOException e ){
			System.err.printf("Error: Unable to open file %s\n",wordListFile);
			return;
		}

		try{
			for (String nextLine = br.readLine(); nextLine != null; nextLine = br.readLine()){
				nextLine = nextLine.trim();
				if (nextLine.equals(""))
					continue; //Ignore blank lines
				//Verify that the line contains only lowercase letters
				for(int ci = 0; ci < nextLine.length(); ci++){
					//The test for lowercase values below is not really a good idea, but
					//unfortunately the provided Character.isLowerCase() method is not
					//strict enough about what is considered a lowercase letter.
					if ( nextLine.charAt(ci) < 'a' || nextLine.charAt(ci) > 'z' ){
						System.err.printf("Error: Word \"%s\" is invalid.\n", nextLine);
						return;
					}
				}
				words.add(nextLine);
			}
		} catch (IOException e){
			System.err.printf("Error reading file\n");
			return;
		}

		/* Find a word ladder between the two specified words. Ensure that the output format matches the assignment exactly. */


		/* Your code here */

		HashMap<String, LinkedList<String>> G = generateGraph(words);

		LinkedList<String> path = BFS(G, startWord, endWord);
		if (path == null) {
			System.out.print("No word ladder found.\n");
			return;
		}

		for (String s: path) {
			System.out.printf("%s\n", s);
		}
	}

	public static LinkedList<String> BFS(HashMap<String, LinkedList<String>> G, String start, String goal) {
		LinkedList<String> Q = new LinkedList<String>();
		HashSet<String> visited = new HashSet<String>();
		HashMap<String, String> parent = new HashMap<String, String>();

		parent.put(start, start);
		Q.add(start);

		String tmp;

		while (!Q.isEmpty()) {
			tmp = Q.remove();
			if (tmp.equals(goal)) {
				return constructPath(tmp, parent);
			}
			if (G.get(tmp) == null) {
				continue;
			}
			for (String n: G.get(tmp)) {
				if (visited.contains(n)) {
					continue;
				}

				if (!Q.contains(n)) {
					parent.put(n, tmp);
					Q.add(n);
				}
			}
			visited.add(tmp);
		}

		return null;
	}

	public static LinkedList<String> constructPath(String last, HashMap<String, String> parent) {
		LinkedList<String> path = new LinkedList<String>();

		while (parent.get(last) != last) {
			path.addFirst(last);
			last = parent.get(last);
		}
		path.addFirst(last);

		return path;
	}

	public static HashMap<String, LinkedList<String>> generateGraph(LinkedList<String> words) {

		// I used a HashMap for speed and ease of implementation,
		// but the worst case complexity of insert and lookup --
		// O(n) -- is sufficient for the purposes of this assignment;
		// the number of elements in the hash table is O(k*26^(k-1))
		// where k is the length of the strings in the word ladder.
		//
		// As of the day of writing this comment, July 31, 2018,
		// the longest published word (according to Wikipedia) is
		// only one thousand nine hundred and nine characters long.
		// https://en.wikipedia.org/wiki/Longest_word_in_English
		// 
		// k is therefore (currently) upper bounded by 1909, so
		// k*26^(k-1) is in \Theta{1}. O(k*26^(k-1)) insert and lookup
		// is therefore trivial.

		HashMap<String, LinkedList<String>> buckets = new HashMap<String, LinkedList<String>>();

		for (String word: words) {
			for (String b: getBuckets(word)) {
				if (!buckets.containsKey(b)) {
					buckets.put(b, new LinkedList<String>());
				}
				buckets.get(b).add(word);
			}
		}

		HashMap<String, LinkedList<String>> G = new HashMap<String, LinkedList<String>>();

		for (LinkedList<String> l: buckets.values()) {
			for (String s1: l) {
				for (String s2: l) {
					if (s1.equals(s2)) {
						continue;
					}
					if (!G.containsKey(s1)) {
						G.put(s1, new LinkedList<String>());
					}
					G.get(s1).add(s2);
				}
			}
		}

		return G;
	}

	public static void printAdjList(HashMap<String, LinkedList<String>> G) {
		StringBuilder sb = new StringBuilder();
		for (String k: G.keySet()) {
			System.out.printf("%s:", k);
			for (String s: G.get(k)) {
				System.out.printf(" %s", s);
			}
			System.out.printf("\n");
		}
	}

	public static boolean isNeighbor(String s1, String s2) {
		assert !s1.equals(s2) : "s1 and s2 cannot be the same";
		assert s1.length() == s2.length() : "s1 and s2 cannot have different length";

		int diff = 0;
		for (int i = 0; i < s1.length(); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				if (diff == 1) return false;
				diff++;
			}
		}
		if (diff == 0) return false;
		return true;
	}

	public static String[] getBuckets(String s) {
		String[] buckets = new String[s.length()];
		for (int i = 0; i < s.length(); i++) {
			buckets[i] = s.substring(0, i) + "_" + s.substring(i+1, s.length());
		}
		return buckets;
	}
}
