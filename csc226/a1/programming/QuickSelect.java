/**
 *
 * @author Rahnuma Islam Nishat
 * September 19, 2018
 * CSC 226 - Fall 2018
 */
import java.util.*;
import java.io.*;

public class QuickSelect {
    
    
    public static int QuickSelect(int[] A, int k) {
		if (A.length == 1) {
			return A[0];
		}
		int p = getPivot(A);
		int[] L = lesserPartition(A, p);
		int[] G = greaterPartition(A, p);
		if (k <= L.length) {
			return QuickSelect(L, k);
		} else if (k == L.length + 1) {
			return p;
		} else {
			return QuickSelect(G, k - L.length - 1);
		}
    }

	// Median-of-medians
	public static int getPivot(int[] A) {
		if (A.length <= 7) {
			Arrays.sort(A);
			return A[A.length/2];
		}
		int d = (A.length/7);
		if (A.length%7 != 0) {
			d++;
		}
		int[] m = new int[d];
		int[] tmp = new int[7];
		int k = 0;
		int j = 0;
		for (int i = 0; i < A.length; i++) {
			tmp[k++%7] = A[i];
			if (k%7 == 0) {
				Arrays.sort(tmp);
				m[j++] = tmp[((k-1)%7)/2];
				// for (int l: tmp) {
				// 	System.out.printf("%02d ", l);
				// }
				// System.out.printf("\n");
			}
		}
		if (k%7 != 0) {
			tmp = Arrays.copyOfRange(tmp, 0, k%7);
			Arrays.sort(tmp);
			m[j] = tmp[((k-1)%7)/2];
			// for (int l: tmp) {
			// 	System.out.printf("%02d ", l);
			// }
			// System.out.printf("\n");
		}
		// for (int l: m) {
		// 	System.out.printf("%02d ", l);
		// }
		// System.out.printf("\n");

		return QuickSelect(m, m.length/2);
	}

	public static int[] greaterPartition(int[] A, int p) {
		int[] G = new int[A.length];
		int j = 0;
		for (int i = 0; i < A.length; i++) {
			if (A[i] > p) {
				G[j++] = A[i];
			}
		}
		return Arrays.copyOfRange(G, 0, j);
	}

	public static int[] lesserPartition(int[] A, int p) {
		int[] L = new int[A.length];
		int j = 0;
		for (int i = 0; i < A.length; i++) {
			if (A[i] < p) {
				L[j++] = A[i];
			}
		}
		return Arrays.copyOfRange(L, 0, j);
	}

// 	public static void main(String[] args) {
// 		int[] a = {1,3,5,8,9,12,13,116,7,18,20,2,23,24};
// 		System.out.printf("%d\n", getPivot(a));
// 		Arrays.sort(a);
// 		System.out.printf("%d\n", a[a.length/2]);
// 		System.out.printf("%s\n", Arrays.toString(a));
// 	}

    public static void main(String[] args) {
        Scanner s;
        int[] array;
        int k;
        if(args.length > 0) {
	    try{
		s = new Scanner(new File(args[0]));
		int n = s.nextInt();
		array = new int[n];
		for(int i = 0; i < n; i++){
		    array[i] = s.nextInt();
		}
	    } catch(java.io.FileNotFoundException e) {
		System.out.printf("Unable to open %s\n",args[0]);
		return;
	    }
	    System.out.printf("Reading input values from %s.\n", args[0]);
        }
	else {
	    s = new Scanner(System.in);
	    System.out.printf("Enter a list of non-negative integers. Enter a negative value to end the list.\n");
	    int temp = s.nextInt();
	    ArrayList<Integer> a = new ArrayList<Integer>();
	    while(temp >= 0) {
		a.add(temp);
		temp=s.nextInt();
	    }
	    array = new int[a.size()];
	    for(int i = 0; i < a.size(); i++) {
		array[i]=a.get(i);
	    }
	    
	    System.out.println("Enter k");
        }
        k = s.nextInt();
        System.out.println("The " + k + "th smallest number is the list is "
			   + QuickSelect(array,k));	
    }
}
