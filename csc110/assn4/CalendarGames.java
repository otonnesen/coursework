/* 
 * Name: Oliver Tonnesen
 * ID: V00885732
 * Date: 10/16/2017
 * Filename: CalendarGames.java
 */
import java.util.Scanner;
/*
 * The class CalendarGames contains methods that 
 * allow for simple date manipulations and a simple
 * interactive computer-guessing game.
 */

public class CalendarGames {

	public static void main(String[] args){
		guessMyBirthday();
	}
/*
 * Converts a month from its integer value to its English name.
 * Input: Integer from corresponding to a month
 * Output: Corresponding month's English name
 */
	public static String monthToString(int month){
		switch(month){
			case 1:
				return "January";
			case 2:
				return "February";
			case 3:
				return "March";
			case 4:
				return "April";
			case 5:
				return "May";
			case 6:
				return "June";
			case 7:
				return "July";
			case 8:
				return "August";
			case 9:
				return "September";
			case 10:
				return "October";
			case 11:
				return "November";
			case 12:
				return "December";
		}
		return "";
	}
/*
 * Determines whether an input year is a leap year.
 * Input: Year
 * Output: true if leap year, false if not leap year
 */
	public static boolean isLeapYear(int year){
		if(year%4!=0) {
			return false;
		} else if(year%100!=0) {
			return true;
		} else if (year%400!=0) {
			return false;
		} else {
			return true;
		}
	}
/*
 * Returns number of days in any given month. 
 * Input:
 *   -Integer corresponding to a month
 *   -Year
 * Output: Number of days in the given month 
 */
	public static int numDaysInMonth(int month, int year){
		if(month==2){
			if(isLeapYear(year)){
				return 29;
			} else {
				return 28;
			}
		/*
		 * All months (apart from February) but April, June, 
		 * September, and November have 31 days, those listed only have 30.
		 */
		} else if(month == 4 || month == 6 || month == 9 || month == 11){ // 
			return 30;
		} else {
			return 31;
		}
	}

/*
 * Uses binary search to find a number in a given range
 * Input:
 *   -Min value
 *   -Max value
 * Output: the found number.
 */

	public static int findDay(int min, int max, int month){
		max+=1;
		Scanner sc = new Scanner(System.in);
		boolean found = false;
		int guess = 0;
		while(found == false){
			guess = (min+max)/2;
			System.out.print("Is your birthday " + monthToString(month) + " " + guess +"? Y/n ");
			String prompt = sc.nextLine();

			if(Math.abs(min-max)<=2){
				if(prompt.equals("Y")) {
					found = true;
					return guess;
				} else if(prompt.equals("n")) {
					System.out.println("Out of range.");
					found = true;
					return -1;
				} else {
					System.out.println("Please enter a valid input.");
				}

			} else if(prompt.equals("Y")){
				found = true;
				return guess;

			} else if(prompt.equals("n")){
				System.out.print("Is your birthday later in the month? Y/n ");
				prompt=sc.nextLine();
				if(prompt.equals("Y")){
					min = guess;
				} else if(prompt.equals("n")){
					max = guess;
				} else {
					System.out.println("Please enter a valid input.");
				}

			} else {
				System.out.println("Please enter a valid input.");
			}
		}

		return -1;
	}

/*
 * A separate function was made so that the user
 * prompt could use the monthToString method. This
 * method is otherwise identical to findDay.
 */

	public static int findMonth(int min, int max){
		max+=1;
		Scanner sc = new Scanner(System.in);
		boolean found = false;
		int guess = 0;
		while(found == false){
			guess = (min+max)/2;
			System.out.print(monthToString(guess) +"? Y/n ");
			String prompt = sc.nextLine();

			if(Math.abs(min-max)<=2){
				if(prompt.equals("Y")) {
					found = true;
					return guess;
				} else if(prompt.equals("n")) {
					System.out.println("Out of range.");
					found = true;
				} else {
					System.out.println("Please enter a valid input.");
				}

			} else if(prompt.equals("Y")){
				found = true;
				return guess;

			} else if(prompt.equals("n")){
				System.out.print("Is your birthday later in the year? Y/n ");
				prompt=sc.nextLine();
				if(prompt.equals("Y")){
					min = guess;
				} else if(prompt.equals("n")){
					max = guess;
				} else {
					System.out.println("Please enter a valid input.");
				}

			} else {
				System.out.println("Please enter a valid input.");
			}
		}

		return -1;
	}

/*
 * A simple interactive computer guessing
 * game which guesses a user's birthday.
 */

	public static void guessMyBirthday(){
		Scanner sc = new Scanner(System.in);
		System.out.println("What month were you born?");
		int month = findMonth(1, 12);
		int day;
		if(month==-1){
			return;
		} else if(month==2){
			System.out.print("What year were you born? ");
			int year = sc.nextInt();
			System.out.println("What day were you born?");
			day = findDay(1, numDaysInMonth(month, year), month);
			if(day==-1){
				return;
			}
		} else {
			
			System.out.println("What day were you born?");
			day = findDay(1, numDaysInMonth(month, 0), month);
			if(day==-1){
				return;
			}
		}
		System.out.println("You were born on " + monthToString(month) + " " + day + ".");
	}
}
