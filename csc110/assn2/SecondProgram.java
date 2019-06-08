/*  Name: Oliver Tonnesen
 *  ID: V00885732
 *  Date: 9/27/2017
 *  Filename: SecondProgram.java
*/
 
/*
 *  The class SecondProgram contains methods
 *  for drawing ASCII art as well as
 *  approximating the area of a circle.
*/

import java.util.Scanner;
public class SecondProgram{

	public static void main(String[] args){
		
		printOwl();
		System.out.println();
		areaCircle();
		
	}
	
	/*
	 *  Prints a text box.
	 *  input: Message to display inside text box
	 *  returns: text box
	*/
	
	public static void printMessage(){
		
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Please enter your message: ");
		String message = sc.nextLine();
		
		for(int i = 0; i < message.length()+4; i++){
			
			System.out.print("*");
			
		}
		
		System.out.println("\n| " + message + " |"); // Sides of text box
		
		for(int i = 0; i < message.length()+4; i++){
			
			System.out.print("*");
			
		}
		System.out.println("\n    \\\n     \\"); // Lines from owl to text box
		
		
	}
	
	/*
	 *  Prints an owl speaking with a text box.
	 *  input: Message to display inside text box
	 *  returns: owl with text box
	*/
	
	public static void printOwl(){
		
		printMessage();
		
		System.out.println("\t  ^...^");
        System.out.println("\t / o,o \\");
        System.out.println("\t |):::(|");
        System.out.println("\t===w=w===");
		
	}
	
	/*
	 *  Approximates the area of a circle given its radius
	 *  input: Circle's radius
	 *  returns: Approximate area of the given circle
	*/
	
	public static void areaCircle(){
		
		Scanner sc = new Scanner(System.in);
		
		// Approximate pi
		double nextTerm = 1;
        double denom = 1;
        double series = 0;
         
        for(int i = 0; i<1000000; i++){
             
            series += (nextTerm/denom);
            denom += 2;
            nextTerm *= -1;
             
        }
         
        series *= 4;
        
		// Approximate area of circle based on approximation of pi
        System.out.print("What is the radius of your circle? ");
		double radius = sc.nextDouble();
		double area = series*radius*radius;
		System.out.println("The area of a circle with radius " + radius + " is " + area + ".");
		
		
	}
	
	

}