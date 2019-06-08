/* 
 * Name: Oliver Tonnesen
 * ID: V00885732
 * Date: 11/16/2017
 * Filename: CutupSongCreator.java
 */

import java.util.Scanner;
import java.io.*;
import java.util.Random;

public class CutupSongCreator {

/*
 * Creates two Scanner objects: one to read input from the console, and one to scan a file.
 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter your file name: ");
		String file = sc.nextLine();
		Scanner input = null;
		try {
			input = new Scanner(new File(file));
		} catch(FileNotFoundException e) {
			System.out.println("File does not exist.");
			System.exit(-1);
		}

		SongLine[] arr = makeArray(input);
		input.close();
		makeSong(arr);
//		listLinesByGenre(arr, "Hero");
//		sortByLineNumber(arr);
//		printArray(arr);
		
	}

/*
 * Creates an array of SongLine from a set of lines read by a Scanner.
 * Input:	A Scanner object linked to an appropriately formatted file
 * Output:	A new array of SongLine objects
 */
	public static SongLine[] makeArray(Scanner input) {
		SongLine[] arr = new SongLine[input.nextInt()];
		String temp;
		int i = 0;
		while(input.hasNext()) {
			arr[i] = new SongLine(input.next(), input.nextInt(), input.nextLine());
			i++;
		}
		
		return arr;
	}

/*
 * Prints out all items in an array that have a genre equal to filterWord
 * Input:
 * 	- songs:	The array of SongLing objects
 * 	-filterWord:	The filter that determines the genre of the SongLines printed 
 * Output: Lines by <filterWorld>
 */
	public static void listLinesByGenre(SongLine[] songs, String filterWord) {
		for(int i = 0; i < songs.length; i++) {
			if(songs[i].getGenre().equals(filterWord)) {
				System.out.println(songs[i]);
			}
		}
	}

/*
 * Prints out elements of an array
 * Input:	array of SongLine objects
 * Output:	Each SongLine object line by ling
 */
	public static void printArray(SongLine[] songs) {

		for(int i = 0; i < songs.length; i++) {
			System.out.println(songs[i]);
		}

	}

/*
 * Sorts an array of SongLine objects by their line number in ascending order
 * Input:	Array of SongLine objects
 * Output:	Sorted array of SongLine objects
 */
	public static void sortByLineNumber(SongLine[] songs) {
		boolean sorted = false;
		while(!sorted) {
			sorted = true;
			for(int i = 0; i < songs.length-1; i++) {
				if(songs[i].getLineNumber() > songs[i+1].getLineNumber()) {
					SongLine temp = songs[i];
					songs[i] = songs[i+1];
					songs[i+1] = temp;
					sorted = false;
				}
			}
		}
	}

/*
 * Prints out a custom 21-line song, randomly selecting words from the array of SongLine objects
 * Input:	The array of SongLine objects from which the words to the song are chosen
 * Output:	Custom song of 21 lines
 */
	public static void makeSong(SongLine[] songs) {
		Random rnd = new Random();
		for(int i = 0; i < 21; i++) {
			System.out.println(songs[rnd.nextInt(songs.length)].getWords());
		}
	}

}
