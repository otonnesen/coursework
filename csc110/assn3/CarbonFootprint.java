/* 
 * Name: Oliver Tonnesen
 * ID: V00885732
 * Date: 10/6/2017
 * Filename: CarbonFootprint.java
*/

import java.util.Scanner;

/*
 * The class CarbonFootprint contains methods
 * to approximate the yearly carbon footprint of 
 * a user based on verious consumption habits.
*/
public class CarbonFootprint{
	
	public static Scanner sc = new Scanner(System.in);

	/*
	 * Calculates yearly emissions based on automobile use.
	 * Input: Kilometres driven per day, fuel efficiency
	 * Output: Yearly automobile emissions in kgCO_2
	*/
	public static double transportationFootprint(double kPD, double effic){
		final int DAYS_IN_YEAR = 365;
		final double KG_CO_2_PER_LITRE_GASOLINE = 2.3;
		double litresUsedPerYear = DAYS_IN_YEAR * kPD / effic;
		double trans = KG_CO_2_PER_LITRE_GASOLINE * litresUsedPerYear;
		return (trans);
	}
	
	/*
	 * Calculates yearly emissions based on home electicity use.
	 * Input: Kilowatts used per month, number of people in household
	 * Output: Yearly electricity emissions in kgCO_2
	*/
	public static double electricityFootprint(double kWhPerMonth, int num){
		final int MONTHS_IN_YEAR = 12;
		final double KG_CO_2_PER_KILOWATT_PER_MONTH = 0.257;	
		double elec = (kWhPerMonth * MONTHS_IN_YEAR * KG_CO_2_PER_KILOWATT_PER_MONTH) / num;
		return (elec);
	}

	/*
	 * Calculates yearly emissions based on food consumption.
	 * Input: Percentage of a person's consumption of different types of foods
	 * Output: Yearly food emissions in kgCO_2
	*/
	public static double foodFootprint(double meat, double dairy, double fv, double carb){
		double kgMeat = meat * 53.1;
		double kgDairy = dairy * 13.8;
		double kgFv = fv * 7.6;
		double kgCarb = carb * 3.1;
		double food = kgMeat + kgDairy + kgFv + kgCarb;
		return (food);
	}

	/*
	 * Calculates total yearly emissions based on transportation, electricity, and food consumption.
	 * Input: Transportation, electricity, and food consumption in kgCO_2
	 * Output: Total yearly emissions in tons CO_2.
	*/
	public static double totalFootprint(double trans, double elec, double food){
		return ((trans + elec + food)/1000);
	}
	public static void main(String[] args){

		System.out.print("How many kilometres do you drive in one day? ");
		double kPD = sc.nextDouble();
		System.out.print("What is the fuel efficiency of your car? ");
		double effic = sc.nextDouble();
		double trans = transportationFootprint(kPD, effic);
		System.out.println("Your automobile-related carbon footprint is " + trans + "kg/year.\n");

		System.out.print("What is your home's average electricity consumption per month in kilowatts?  ");
		double kWhPerMonth = sc.nextDouble();
		System.out.print("How many people live in your home? ");
		int num = sc.nextInt();
		double elec = electricityFootprint(kWhPerMonth, num);
		System.out.println("Your electricity-related carbon footprint is " + elec + "kg/year.\n");

		System.out.print("What percentage of your diet consists of meat? ");
		double meat = sc.nextDouble() / 100;
		System.out.print("What percentage of your diet consists of dairy? ");
		double dairy = sc.nextDouble() / 100;
		System.out.print("What percentage of your diet consists of fruits and vegetables? ");
		double fv = sc.nextDouble() / 100;
		System.out.print("What percentage of your diet consists of carb? ");
		double carb = sc.nextDouble() / 100;
		double food = foodFootprint(meat, dairy, fv, carb);
		System.out.println("Your food-related carbon footprint is " + food + "kg/year.\n");

		System.out.println("Your total annual carbon footprint is " + totalFootprint(trans, elec, food) + " metric tons of CO2 per year.");
		
	}
	
}


