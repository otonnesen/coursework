/*	Name: Oliver Tonnesen
 *	ID: V00885732
 *	Date: 9/16/2017
 *	Filename: FirstProgram.java
*/

/*
 *	The class FirstProgram contains methods
 *	for drawing ASCII art as well as
 *	approximating the number pi.
*/


public class FirstProgram{

	public static void main(String[] args){
		
		printFrog();
		System.out.println();
		System.out.println();
		printOwl();
		System.out.println();
		System.out.println();
		approxPi();
		
	}

	/*
	 * Prints an ASCII frog.
	 * input: none
	 * returns: ASCII frog
	*/
	
	public static void printFrog(){
		
		System.out.println("  @..@");
		System.out.println(" (----)");
		System.out.println("( >__< )");
		System.out.println("\"\"    \"\"");
		
	}

	/*
	 * Prints an ASCII owl.
	 * input: none
	 * returns: ASCII owl
	*/
	
	public static void printOwl(){
		
		System.out.println("  ^...^");
		System.out.println(" / o,o \\");
		System.out.println(" |):::(|");
		System.out.println("===w=w===");
		
	}
	
	/*
	 * Approximates the number pi.
	 * input: none
	 * returns: approximation of pi
	*/
	
	public static void approxPi(){
		
		double nextTerm = 1;
		double denom = 1;
		double series = 0;
		
		for(int i = 0; i<8; i++){
			
			series += (nextTerm/denom);
			denom += 2;
			nextTerm *= -1;
			
		}
		
		series *= 4;
		
		System.out.println("Pi estimate: " + series);
		
	}
	
}
