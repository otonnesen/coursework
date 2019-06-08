/* MST.java
   CSC 226 - Fall 2018
   Problem Set 2 - Template for Minimum Spanning Tree algorithm

   The assignment is to implement the mst() method below, using Kruskal's algorithm
   equipped with the Weighted Quick-Union version of Union-Find. The mst() method computes
   a minimum spanning tree of the provided graph and returns the total weight
   of the tree. To receive full marks, the implementation must run in time O(m log m)
   on a graph with n vertices and m edges.

   This template includes some testing code to help verify the implementation.
   Input graphs can be provided with standard input or read from a file.

   To provide test inputs with standard input, run the program with
       java MST
   To terminate the input, use Ctrl-D (which signals EOF).

   To read test inputs from a file (e.g. graphs.txt), run the program with
       java MST graphs.txt

   The input format for both methods is the same. Input consists
   of a series of graphs in the following format:

       <number of vertices>
       <adjacency matrix row 1>
       ...
       <adjacency matrix row n>

   For example, a path on 3 vertices where one edge has weight 1 and the other
   edge has weight 2 would be represented by the following

   3
   0 1 0
   1 0 2
   0 2 0

   An input file can contain an unlimited number of graphs; each will be processed separately.

   NOTE: For the purpose of marking, we consider the runtime (time complexity)
         of your implementation to be based only on the work done starting from
		 the mst() method. That is, do not not be concerned with the fact that
		 the current main method reads in a file that encodes graphs via an
		 adjacency matrix (which takes time O(n^2) for a graph of n vertices).

   (originally from B. Bird - 03/11/2012)
   (revised by N. Mehta - 10/9/2018)
*/

import java.util.Scanner;
import java.io.File;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import java.util.concurrent.TimeUnit;

public class MST {

	private static class Edge implements Comparable<Edge> {
		int p;
		int q;
		int weight;
		Edge(int p, int q, int weight) {
			this.p = p;
			this.q = q;
			this.weight = weight;
		}

		public int compareTo(Edge e) {
			return this.weight - e.weight;
		}
	}

	private static class UF {
		private int[] tree;
		private int[] weight;

		UF(int n) {
			this.tree = new int[n];
			for (int i = 0; i < n; i++) {
				this.tree[i] = i;
			}
			this.weight = new int[n];
		}

		void union(int p, int q) { // Weighted Quick Union
			if (this.weight[p] > this.weight[q]) {
				this.tree[this.find(q)] = this.find(p);
				this.weight[p]++;
			} else {
				this.tree[this.find(p)] = this.find(q);
				this.weight[q]++;
			}
		}

		int find(int p) {
			int i;
			for (i = p; i != this.tree[i]; i = this.tree[i], this.tree[i] = this.tree[this.tree[i]]); // Path Compression
			return i;
		}

		boolean connected(int p, int q) {
			return find(p) == find(q);
		}
	}


    /* mst(adj)
       Given an adjacency matrix adj for an undirected, weighted graph, return the total weight
       of all edges in a minimum spanning tree.

       The number of vertices is adj.length
       For vertex i:
         adj[i].length is the number of edges
         adj[i][j] is an int[2] that stores the j'th edge for vertex i, where:
           the edge has endpoints i and adj[i][j][0]
           the edge weight is adj[i][j][1] and assumed to be a positive integer
    */
    static int mst(int[][][] adj) {
		int n = adj.length;

		/* Find a minimum spanning tree using Kruskal's algorithm */
		/* (You may add extra functions if necessary) */

		/* ... Your code here ... */

		ArrayList<Edge> tmp = new ArrayList<Edge>();

		for (int i = 0; i < adj.length; i++) {
			for (int j = 0; j < adj[i].length; j++) {
				if (adj[i][j][0]>i) { // Only reads edges from upper triangle of matrix since the given graphs are undirected
					tmp.add(new Edge(i, adj[i][j][0], adj[i][j][1]));
				}
			}
		}
		Edge[] edges = tmp.toArray(new Edge[0]);
		Arrays.sort(edges); // Sorted Edge[] from min. to max. weight


		UF uf = new UF(n); // Disjoint set

		int edgesUsed = 0; // Counter for edge array
		Edge[] tree = new Edge[n-1]; // MST

		for (int i = 0; i < edges.length && edgesUsed < n-1; i++) {
			if (uf.connected(edges[i].p, edges[i].q)) continue;
			uf.union(edges[i].p, edges[i].q);
			tree[edgesUsed++] = edges[i];
		}

		/* Add the weight of each edge in the minimum spanning
		 * to totalWeight, which will store the total weight of the tree.
		*/
		int totalWeight = 0;
		/* ... Your code here ... */

		for (Edge e: tree) {
			totalWeight += e.weight;
		}

		return totalWeight;

    }


	public static void main(String[] args) {
		/* Code to test your implementation */
		/* You may modify this, but nothing in this function will be marked */

		int graphNum = 0;
		Scanner s;

		if (args.length > 0) {
			//If a file argument was provided on the command line, read from the file
			try {
				s = new Scanner(new File(args[0]));
			}
				catch(java.io.FileNotFoundException e) {
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}
		else {
			//Otherwise, read from standard input
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}

		//Read graphs until EOF is encountered (or an error occurs)
		while(true) {
			graphNum++;
			if(!s.hasNextInt()) {
				break;
			}
			System.out.printf("Reading graph %d\n",graphNum);
			int n = s.nextInt();

			int[][][] adj = new int[n][][];




			int valuesRead = 0;
			for (int i = 0; i < n && s.hasNextInt(); i++) {
				LinkedList<int[]> edgeList = new LinkedList<int[]>();
				for (int j = 0; j < n && s.hasNextInt(); j++) {
					int weight = s.nextInt();
					if(weight > 0) {
						edgeList.add(new int[]{j, weight});
					}
					valuesRead++;
				}
				adj[i] = new int[edgeList.size()][2];
				Iterator<int[]> it = edgeList.iterator();
				for(int k = 0; k < edgeList.size(); k++) {
					adj[i][k] = it.next();
				}
			}
			if (valuesRead < n * n) {
				System.out.printf("Adjacency matrix for graph %d contains too few values.\n",graphNum);
				break;
			}

			// // output the adjacency list representation of the graph
			// for(int i = 0; i < n; i++) {
			// 	System.out.print(i + ": ");
			// 	for(int j = 0; j < adj[i].length; j++) {
			// 	    System.out.print("(" + adj[i][j][0] + ", " + adj[i][j][1] + ") ");
			// 	}
			// 	System.out.print("\n");
			// }

			int totalWeight = mst(adj);
			System.out.printf("Graph %d: Total weight of MST is %d\n",graphNum,totalWeight);


		}
	}
}
