/**
 * OperatorTokenizer.java
 * @author B. Bultena
 * Created for exclusive use by CSC115 students
 * at UVic in the Spring 2018 term.
 * This code is not to be distributed without written consent 
 * by the author.
 */ 

import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * OperatorTokenizer creates an iterator of tokens that are separated from 
 * and include arithmetic operators.
 * Round parens are also separated out as tokens.
 * The list of acceptable operators are detailed in the Operator class
 */
public class OperatorTokenizer implements Tokenizer {

	private String[] tokens;
	private int currTokenIndex;

	/**
	 * Separates the operators and parens from the rest of the expression
	 * to create a set of String tokens, in the same left-to-right order as found
	 * in the input string.
	 * @param expression If this string is a valid arithmetic expression, the tokens
	 * will be the operands and operators and parens of this expression.
	 * If this is not a valid expression, the operators and parens will be identified
	 * as tokens, and the left-overs are separated as tokens.
 	 * @param expression The string to be tokenized.
 	 */
	public OperatorTokenizer(String expression) {
		String[] bigContainer = new String[expression.length()];

		int numTokens = parseOperatorsParens(bigContainer,expression);
		// make the array fit the number of tokens.
		setupTokenArray(bigContainer,numTokens);
		// Now we are ready to iterate through the tokens
		currTokenIndex = 0;
	}

	/*
 	 * A private method that extracts the actual operators and parens.
	 * input: array is an array that is bigger than the number of tokens.
	 * input: The original expression that will be parsed.
	 * output: The actual number of tokens, that is determined in the process.
	 */
	private int parseOperatorsParens(String[] array, String expression) {
		// A regular expression that looks for operators or parens.
		Pattern opParenPattern = Pattern.compile("[-+*/^()]");
		Matcher finder = opParenPattern.matcher(expression);

		String opOrParen,between; 
		int lastBetweenIndex = 0;
		String lastOpOrParen = "";
		int numItems = 0;

		// find all occurrences of a matched substring
		// that matches the pattern in the expression.
		while(finder.find()) {
			opOrParen = finder.group(); 
			// get the substring between the operators and parens
			between = expression.substring(lastBetweenIndex,finder.start());
			between = between.trim(); //removes whitespace around the substring.
			// The very first '-' or a '-' that follows another operator is a negative sign and not an operator.
			if (opOrParen.charAt(0) == '-') {
				if (finder.start() == 0 || 
					!lastOpOrParen.equals(")") && between.equals("")) {
						continue; // ignore this minus sign
				}
			}
			// between can be "" when an operator follows a closed paren
			if (between.length() != 0) {
				array[numItems++] = between;
			}
			lastBetweenIndex = finder.end();
			array[numItems++] = opOrParen;
			lastOpOrParen = opOrParen;
		}
		// After all the operators and parens found, finish off what is left.
		if (lastBetweenIndex < expression.length()) {
			between = expression.substring(lastBetweenIndex,expression.length());
			between = between.trim();
			array[numItems++] = between;
		}
		return numItems;
	}

	/*
	 * A private helper method that creates the data field array that is the actual size
	 * of the number of tokens.
	 * It is not necessary, but it makes it a little easier to iterate through the whole
	 * array, knowing there is an element for every index in the array.
	 */
	private void setupTokenArray(String[] array, int size) {
		tokens = new String[size];
		for (int i=0; i<size; i++) {
			tokens[i] = array[i];
		}
	}

/********  Tokenizer required methods **********/ 
	public void reset() {
		currTokenIndex = 0;
	}

	public int numTokens() {
		return tokens.length;
	}

	/**
	 * The format is a list of tokens on a single line,
	 * delimited by a single space.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(tokens.length*10);
		for (int i=0; i<tokens.length-1; i++) {
			sb.append(tokens[i]+' ');
		}
		if (tokens.length > 0) {
			sb.append(tokens[tokens.length-1]);
		}
		return sb.toString();
	}

/************ Iterator required methods *****************/

	public boolean hasNext() {
		return currTokenIndex != tokens.length;
	}

	public String next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more tokens");
		}
		return tokens[currTokenIndex++];
	}

	/**
 	 * This method is not supported on an immutable list.
	 */
	public void remove() {
		throw new UnsupportedOperationException("Tokens may not be removed");
	}

/********************* end of Iterator methods ***********/

	/**
	 * Used specifically for internal testing during the creation
	 * of this class.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		String test1 = "3*(4+2)-4*(-6--3)";
		// a bad expression
		String test2 = "hello+3still-(dog/cat)+3";
		// even worse:
		String test3 = "3-hello?(bbb/*=\")";
		OperatorTokenizer infix1 = new OperatorTokenizer(test1);
		OperatorTokenizer infix2 = new OperatorTokenizer(test2);
		OperatorTokenizer infix3 = new OperatorTokenizer(test3);
		System.out.println("The print outs:");
		System.out.println(infix1);
		System.out.println(infix2);
		System.out.println(infix3);
		System.out.println("The valid expression tokens accessed individually:");
		while(infix1.hasNext()) {
			System.out.println("next token: "+infix1.next());
		}
		System.out.println("Done");
	}
}
	
