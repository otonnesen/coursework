// TODO Better documentation
// Oliver Tonnesen
// V00885732
// CSC 225 Programming Assignment 1
// May 11, 2018

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.StringBuilder;

import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException;
import java.io.IOException;

public class Aggregate {

	// Multiple Pair classes are implemented to avoid trying to use generic arrays
	public static class Pair implements Comparable<Pair> {
		public String key;
		public String value;

		public Pair (String key, String value) {
			this.key = key;
			this.value = value;
		}

		public int compareTo(Pair p) {
			return (this.key+this.value).compareTo((p.key+p.value));
		}

		public int compareKey(Pair p) {
			return this.key.compareTo(p.key);
		}
	}

	public static class IntPair {
		public String key;
		public int value;

		public IntPair (String key, int value) {
			this.key = key;
			this.value = value;
		}
	}

	public static class DoublePair {
		public String key;
		public double value;

		public DoublePair (String key, double value) {
			this.key = key;
			this.value = value;
		}
	}

	// Multiple makeCSV methods are implemented for the different Pair classes
	public static String makeCSV(String[] colNames, Pair[] table) {
		StringBuilder s = new StringBuilder();
		for (String i: colNames) {
			s.append(i+",");
		}
		s.setCharAt(s.length()-1, '\n');
		for (Pair i: table) {
			s.append(i.key+","+i.value+"\n");
		}
		s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	public static String makeCSV(String[] colNames, IntPair[] table) {
		StringBuilder s = new StringBuilder();
		for (String i: colNames) {
			s.append(i+",");
		}
		s.setCharAt(s.length()-1, '\n');
		for (IntPair i: table) {
			s.append(i.key+","+i.value+"\n");
		}
		s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	public static String makeCSV(String[] colNames, DoublePair[] table) {
		StringBuilder s = new StringBuilder();
		for (String i: colNames) {
			s.append(i+",");
		}
		s.setCharAt(s.length()-1, '\n');
		for (DoublePair i: table) {
			if (i.value != Math.floor(i.value)) {
				s.append(i.key+","+String.format("%.2f", i.value)+"\n");
			} else {
				s.append(i.key+","+String.format("%.0f", i.value)+"\n");
			}
		}
		s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	// Multiple resize methods are implemented for the different Pair classes
	public static IntPair[] resize(IntPair[] p) {
		IntPair[] out = new IntPair[p.length*2];
		for (int i = 0; i < p.length; i++) {
			out[i] = new IntPair(p[i].key, p[i].value);
		}
		return out;
	}

	public static DoublePair[] resize(DoublePair[] p) {
		DoublePair[] out = new DoublePair[p.length*2];
		for (int i = 0; i < p.length; i++) {
			out[i] = new DoublePair(p[i].key, p[i].value);
		}
		return out;
	}

	public static int[] resize(int[] p) {
		int[] out = new int[p.length*2];
		for (int i = 0; i < p.length; i++) {
			out[i] = p[i];
		}
		return out;
	}

	public static String[] resize(String[] p) {
		String[] out = new String[p.length*2];
		for (int i = 0; i < p.length; i++) {
			out[i] = p[i];
		}
		return out;
	}

	public static Pair[] resize(Pair[] p) {
		Pair[] out = new Pair[p.length*2];
		for (int i = 0; i < p.length; i++) {
			out[i] = new Pair(p[i].key, p[i].value);
		}
		return out;
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			throw new IllegalArgumentException("Not enough arguments.");
		}
		BufferedReader input;
		// Checking condition 2.
		try {
			input = new BufferedReader(new FileReader(args[2]));
			// input.useDelimiter(",|\\n");
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File not found.");
		}
		String[] groupCols = new String[args.length-3];
		for (int i = 0; i < groupCols.length; i++) {
			groupCols[i] = args[i+3];
		}
		String[] colNames;
		try {
			colNames = input.readLine().split(",");
		} catch (IOException e) {
			throw new IllegalArgumentException("Error reading file.");
		}
		String aggCol = args[1];

		// Checks against the following five conditions:
		// 	1. The <function> argument is not one of 'count', 'sum',
		// 	'avg', or 'count_distinct'.
		// 	2. The input file does not exist, cannot be opened or is
		// 	not in the correct format.
		// 	3. The aggregation column or any of the grouping columns
		// 	do not exist in the input file.
		// 	4. A column is used as both a grouping column and an
		// 	aggregation column.
		// 	5. When the <function> argument is 'sum' or 'avg' and the
		// 	aggregation column contains any non-numerical data.

		// Checking condition 1.
		// See below

		// Checking condition 2.
		// See above

		// Checking condition 3
		// 	Aggregation column
		boolean validName = false;
		for (String i: colNames) {
			if (aggCol.equals(i)) {
				validName = true;
				break;
			}
		}
		if (!validName) {
			throw new IllegalArgumentException("Invalid aggregation column '"+
					aggCol+"'.");
		}

		// 	Grouping columns
		for (String i: groupCols) {
			validName = false;
			for (String j: colNames) {
				if (i.equals(j)) {
					validName = true;
					break;
				}
			}
			if (!validName) {
			throw new IllegalArgumentException("Invalid grouping column '"+i+"'.");
			}
		}

		// Checking condition 4.
		for (String i: groupCols) {
			if (aggCol.equals(i)) {
				throw new IllegalArgumentException("Cannot use column '"
						+ aggCol +
						"' as both the aggregation column and a grouping column.");
			}
		}

		// Checking condition 5.
		// See sum() and avg() below

		Pair[] table = makeTable(input, colNames, groupCols, aggCol);
		// String[] of grouping columns in order, followed by the aggregation column
		String[] outputColNames = new String[groupCols.length+1];
		for (int i = 0; i < groupCols.length; i++) {
			outputColNames[i] = groupCols[i];
		}
		outputColNames[outputColNames.length-1] = aggCol;

		Arrays.sort(table);

		// Checking condition 1.
		switch (args[0]) {
			case "count":
				System.out.println(count(outputColNames, table));
				break;
			case "avg":
				System.out.println(avg(outputColNames, table));
				break;
			case "sum":
				System.out.println(sum(outputColNames, table));
				break;
			case "count_distinct":
				System.out.println(count_distinct(outputColNames, table));
				break;
			default:
				throw new IllegalArgumentException("Invalid function.");
		}

	}

	// Makes Pair[] using only grouping columns and aggregation column
	public static Pair[] makeTable(BufferedReader input,
		String[] colNames,
		String[] groupCols,
		String aggCol) {

		String t; // Temp variable to hold a single line of raw data
		String[] tmp; // Temp variable to hold each comma-delimited token of data from a single line

		int[] colIndices = new int[groupCols.length]; // Array of grouping column indices
		int aggColIndex = -1;

		Pair[] out = new Pair[1];
		int size = 0; // Stores end of column when resizing

		StringBuilder s;

		// Determine the order of columns given in the command-line arguments
		for (int i = 0; i < groupCols.length; i++) {
			for (int j = 0; j < colNames.length; j++) {
				if (groupCols[i].equals(colNames[j])) {
					colIndices[i] = j;
				} else if (aggCol.equals(colNames[j])) {
					aggColIndex = j;
				}
			}
		}

		// Fill Pair[]
		try {
			t = input.readLine();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error reading file.");
		}
		while (t != null) {

			if (size == out.length) {
				out = resize(out);
			}

			s = new StringBuilder();
			tmp = t.split(",");

			for (int i = 0; i < colIndices.length; i++) {
				s.append(tmp[colIndices[i]]+",");
			}

			s.deleteCharAt(s.length()-1);
			out[size++] = new Pair(s.toString(), tmp[aggColIndex]);

			try {
				t = input.readLine();
			} catch (IOException e) {
				throw new IllegalArgumentException("Error reading file.");
			}

		}
		return Arrays.copyOfRange(out, 0, size);
	}

	public static String count(String[] colNames, Pair[] table) {
		Arrays.sort(table); // nlogn
		
		Pair prev = table[0];
		IntPair[] out = new IntPair[1];
		int inc = 0;
		out[inc] = new IntPair(prev.key, 1);

		for (int i = 1; i < table.length; i++) { // n
			if (table[i].compareKey(prev) == 0) {
				out[inc].value++;
			} else {
				if (inc+1 == out.length) {
					out = resize(out);
				}
				out[++inc] = new IntPair(table[i].key, 1);
			}
			prev = table[i];
		}

		// Rename aggregation column
		colNames[colNames.length-1] = "count("+colNames[colNames.length-1]+")";

		return makeCSV(colNames, Arrays.copyOfRange(out, 0, inc+1));
	}

	public static String avg(String[] colNames, Pair[] table) {
		Arrays.sort(table); // nlogn

		Pair prev = table[0];
		DoublePair[] out = new DoublePair[1];
		int[] count = new int[1];
		int inc = 0;
		try {
			out[inc] = new DoublePair(prev.key, Double.parseDouble(prev.value));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Cannot average non-numerical column '"+colNames[colNames.length-1]+"'.");
		}
		count[inc] = 1;

		for (int i = 1; i < table.length; i++) { // n
			if (table[i].compareKey(prev) == 0) {
				try {
					out[inc].value += Double.parseDouble(table[i].value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Cannot average non-numerical column '"+colNames[colNames.length-1]+"'.");
				}
				count[inc]++;
			} else {
				if (inc+1 == out.length) {
					out = resize(out);
				}
				if (inc+1 == count.length) {
					count = resize(count);
				}
				try {
					out[++inc] = new DoublePair(table[i].key, Double.parseDouble(table[i].value));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Cannot average non-numerical column '"+colNames[colNames.length-1]+"'.");
				}
				count[inc] = 1;
			}
			prev = table[i];
		}

		for (int i = 0; i < inc+1; i++) {
			out[i].value = out[i].value/count[i];
		}

		// Rename aggregation column
		colNames[colNames.length-1] = "avg("+colNames[colNames.length-1]+")";

		return makeCSV(colNames, Arrays.copyOfRange(out, 0, inc+1));
	}

	public static String sum(String[] colNames, Pair[] table) {
		Arrays.sort(table); // nlogn

		Pair prev = table[0];
		DoublePair[] out = new DoublePair[1];
		int inc = 0;
		try{
			out[inc] = new DoublePair(prev.key, Double.parseDouble(prev.value));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Cannot sum non-numerical column '"+colNames[colNames.length-1]+"'.");
		}

		for (int i = 1; i < table.length; i++) { // n
			if (table[i].compareKey(prev) == 0) {
				try {
					out[inc].value += Double.parseDouble(table[i].value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Cannot sum non-numerical column '"+colNames[colNames.length-1]+"'.");
				}
			} else {
				if (inc+1 == out.length) {
					out = resize(out);
				}
				try {
					out[++inc] = new DoublePair(table[i].key, Double.parseDouble(table[i].value));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Cannot sum non-numerical column '"+colNames[colNames.length-1]+"'.");
				}
			}
			prev = table[i];
		}

		// Rename aggregation column
		colNames[colNames.length-1] = "sum("+colNames[colNames.length-1]+")";

		return makeCSV(colNames, Arrays.copyOfRange(out, 0, inc+1));
	}

	public static String count_distinct(String[] colNames, Pair[] table) {
		Arrays.sort(table); // nlogn

		Pair prev = table[0];
		IntPair[] out = new IntPair[1];
		int inc = 0;

		out[inc] = new IntPair(prev.key, 1);
		
		for (int i = 1; i < table.length; i++) { // n
			if (table[i].compareKey(prev) == 0) {
				if (table[i].compareTo(prev) != 0) {
					out[inc].value++;
				}
			} else {
				if (inc+1 == out.length) {
					out = resize(out);
				}
				out[++inc] = new IntPair(table[i].key, 1);
			}
			
			prev = table[i];
		}
		// Rename aggregation column
		colNames[colNames.length-1] = "count_distinct("+colNames[colNames.length-1]+")";

		return makeCSV(colNames, Arrays.copyOfRange(out, 0, inc+1));
	}
}
