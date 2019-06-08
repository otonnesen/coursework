/*
 * Name: Oliver Tonnesen
 * ID: V00885732
 * Filename: DNASequencing.java
 */
import java.util.Scanner;
/*
 * The class DNASequencing contains methods
 * to analyze sequences of DNA strings.
 */
public class DNASequencing {
	
/*
 * Main function
 * Contains testing for other
 * methods in DNASequencing
 * class.
 */
	public static void main(String[] args) {
/*
		// printArray test
		String[] test1 = {"AC", "TACG", "AAC", "GTT"};
		printArray(test1);

		// findLongest test
		String[] test2 = {"TG", "TGA", "ACT", "GA", "CCT"};
		String longest = findLongest(test2);
		System.out.println("The longest String is "+longest);

		// findFrequency test
		String[] test3 = {"ACT", "TGA", "TCT", "CTC", "ACT", "GTAC"};
		int frequency = findFrequency("ACT", test3);
		System.out.println("The sequence 'ACT' appears " + frequency + " times.");

		// hasMutation/hasCharacterRun test
		String test4 = "AATG";
		System.out.println(hasMutation(test4));

		// countTotalMutations test
		String[] test5 = {"AATGG", "CATG", "GG", "TGCAA", "CACGT", "GACT"};
		System.out.println("There are " + countTotalMutations(test5) + " mutations.");

		// findFreqWithMutations/isMutationOf test
		String[] test6 = {"AAGT", "ACGT", "AGT", "AGGGTT", "AAAAAGTTTT", "GACT", "GTAC", "GTT", "AC", "TACG"};
		System.out.println("The sequence AGT has " + findFreqWithMutations("AGT", test6) + " mutations present.");
*/
		String[] test7 = {"AAACGG","TCC","GC","TTTC","TC","TTA","CCA"};
		System.out.println("The sequence TC has " + findFreqWithMutations("TC", test7) + " mutations present.");

	}
/*
 * Prints out contents of an array, one item per line.
 * Input: Array of Strings
 * Output: Each String in array, line by line
 */
	public static void printArray(String[] strings) {

		for(int i = 0; i < strings.length; i++) {
			System.out.println(strings[i]);
		}

	}
/*
 * Finds the first occurrence of the longest of an array of Strings.
 * Input: Array of Strings
 * Output: Longest String in the array
 */
	public static String findLongest(String[] strings) {

		int longest = 0;

		for(int i = 1; i < strings.length; i++) {
			if(strings[i].length() > strings[longest].length()) {
				longest = i;
			}
		
		}

		return strings[longest];

	}
/*
 * Determines the number of times a particular item occurs within an array of Strings.
 * Input: Array of Strings, target String of which to find the frequency
 * Output: Number of times the target String occurs in the given array
 */
	public static int findFrequency(String target, String[] strings) {

		int sum = 0;
		for(int i = 0; i < strings.length; i++) {
			if(strings[i].equals(target)) {
				sum++;
			}

		}

		return sum;

	}
/*
 * Determines whether a given DNA sequence contains any repeating characters.
 * Input: String
 * Output: Boolean value corresponding to the presence of repeating characters in the given String
 */
	public static boolean hasCharacterRun(String sequence) {

		char[] seqArray = sequence.toCharArray();
		for(int i = 0; i < seqArray.length-1; i++) {

			if(seqArray[i] == seqArray[i+1]) {

				return true;

			}

		}

		return false;
		
	}
/*
 * Uses method hasCharacterRun to determine whether a given DNA sequence contains a mutation.
 * Input: DNA sequence
 * Output: Boolean value corresponding to the presence of a mutation in the given DNA sequence
 */
	public static boolean hasMutation(String sequence) {

		return(hasCharacterRun(sequence));

	}
/*
 * Counts the number of mutated DNA sequences that occur within an array.
 * Input: Array of DNA sequences
 * Output: Number of times a mutated DNA sequence occurs in the array
 */
	public static int countTotalMutations(String[] seqArray) {

		int sum = 0;
		for(int i = 0; i < seqArray.length; i++) {
			if(hasMutation(seqArray[i])) {
				sum++;
			}
		}

		return sum;

	}
/*
 * Determines whether a given potential mutation is in fact a mutation of a target unmutated DNA sequence
 * Input:
 * 	-orig: unmutated DNA sequence of 2 to 4 characters
 * 	-muta: potential mutation of orig to be tested
 * Output: Boolean value corresponding to whether or not muta is a mutation of orig
 */
	public static boolean isMutationOf(String orig, String muta) {

		if(!hasMutation(muta)) {
			return false;
		}
		char[] reduced = new char[4]; // Unmutated sequence must be four or less
		char[] mutaArray = muta.toCharArray();

		// Remove repeated characters from muta so it can be compared to orig
		char current = mutaArray[0];
		int index = 0;
		boolean same = false;
		for(int i = 0; i < mutaArray.length; i++) {
			if(current == mutaArray[i] && !same) {
				same = true;
			} else if(current != mutaArray[i]) {
				same = false;
				reduced[index] = current;
				index++;
				current = mutaArray[i];
			}
		}
		reduced[index] = current;

		// Convert char[] to String
		String reducedStr = "";
		for(int i = 0; i < reduced.length; i++) {
			if(reduced[i]!='\u0000') { // Remove null characters from array in case the length of the DNA sequence was less than four
				reducedStr += reduced[i];
			}
		}
		

		if(orig.equals(reducedStr)){
			return true;
		} else {
			return false;
		}

	}
/*
 * Determines the number of times a particular DNA sequence occurs within an array of DNA sequences.
 * Input: Array of DNA sequences, target unmutated DNA sequence of which to find mutations
 * Output: Number of occurrences of mutations of the target unmutated DNA sequence
 */
	public static int findFreqWithMutations(String target, String[] seqArray) {

		int sum = 0;
		for(int i = 0; i < seqArray.length; i++) {
			if(isMutationOf(target, seqArray[i])) {
				sum++;
			}
		}

		return sum;

	}

}
