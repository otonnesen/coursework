// Oliver Tonnesen
// V00885732

import java.util.Scanner;
import java.util.Arrays;

public class sort3 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int[] x = new int[3];
		System.out.print("Enter a number: ");
		x[0] = sc.nextInt();
		System.out.print("Enter a number: ");
		x[1] = sc.nextInt();
		System.out.print("Enter a number: ");
		x[2] = sc.nextInt();
		Arrays.sort(x);
		for(int i = 0; i < x.length; i++) {
			System.out.println(x[i]); 
		}



	}

}
