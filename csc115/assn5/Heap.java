// Name: Oliver Tonnesen
// ID: V00885732
// Date: March 30, 2018
// Filename: Heap.java
// Details: CSC 115 Assignment 5

/**
 * Heap.java
 * Provide the necessary header information here, 
 * making sure to delete these lines.
 */

import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

// This is the beginning of the source code to help you get started.
// Do not change the following:

public class Heap<E extends Comparable<E>> {

	private ArrayList<E> heap;
	private int size;
	private static final int CAPACITY = 100;

	/**
	 * Creates an empty heap.
	 */
	public Heap() {
		heap = new ArrayList<E>(CAPACITY);
		for (int i=0; i<CAPACITY; i++) {
			heap.add(null);
		}
	}

	// Method:		getRootItem
	// Description:		Removes the element that is currently
	// 			at the root of the heap.
	// Input:		None
	// Output:		Element at root position in heap.
	// Throws:		NoSuchElementException
	// 			  - if the heap is empty.

	public E getRootItem() throws NoSuchElementException {
		if (this.isEmpty()) {
			throw new NoSuchElementException("Heap is empty.");
		}
		E tmp = this.heap.get(0);
		this.heap.set(this.size, null);
		this.heap.set(0, this.heap.get(--this.size));
		rebuild(0);
		return tmp;
	}

	// Recursive helper method

	private void rebuild(int root) {
		int leftChild = root*2+1;
		int rightChild = root*2+2;
		int child = leftChild;
		// System.out.println(this.heap.get(leftChild));
		// System.out.println(this.heap.get(rightChild));
		if (this.heap.get(leftChild) != null) {
			if (this.heap.get(rightChild) != null &&
				this.heap.get(rightChild)
				.compareTo(this.heap.get(leftChild)) < 0) {
					child = rightChild;
			}
			// System.out.println(this.heap.get(root));
			// System.out.println(this.heap.get(child));
			if (this.heap.get(root).compareTo(this.heap.get(child)) > 0) {
				E tmp = this.heap.get(root);
				this.heap.set(root, this.heap.get(child));
				this.heap.set(child, tmp);
				rebuild(child);
			}
		}
	}

	// Method:		insert
	// Description:		Adds element 'element' to the heap, placing
	// 			    it into the only correct position.
	// Input:
	// 	element    -	The element to insert.
	// Output:		None

	public void insert(E element) {
		int place = this.size;
		this.heap.set(size++, element);
		int parent = (place-1)/2;
		while ( parent >= 0 &&
			this.heap.get(place).compareTo(this.heap.get(parent)) < 0) {

			E tmp = this.heap.get(parent);
			this.heap.set(parent, this.heap.get(place));
			this.heap.set(place, tmp);

			place = parent;
			parent = (place-1)/2;
		}
		// for(E i: this.heap.subList(0, this.size)) {
		// 	System.out.println(i);
		// }
	}


	// Method:		isEmpty
	// Description:		Determines whether or not the heap is empty.
	// Input:		None
	// Output:		True if the heap is empty, false if not.

	public boolean isEmpty() {
		return this.size == 0;
	}

	// Method:		size
	// Description:		Determines the size of the heap.
	// Input:		None
	// Output:		The number of elements in the heap.

	public int size() {
		return this.size;
	}

	// Method:		main
	// Description:		Used for internal testing only.
	// Input:		
	// 	args    -	Not used.
	// Output:		None

	public static void main(String[] args) {
		// Heap<ERPatient> h = new Heap<ERPatient>();
		// h.insert(new ERPatient("Number", "7", "Ambulatory"));
		// h.insert(new ERPatient("Number", "1", "Life-threatening"));
		// h.insert(new ERPatient("Number", "3", "Major fracture"));
		// h.insert(new ERPatient("Number", "4", "Acute"));
		// h.insert(new ERPatient("Number", "2", "Life-threatening"));
		// h.insert(new ERPatient("Number", "5", "Chronic"));
		// h.insert(new ERPatient("Number", "6", "Chronic"));

		// h.insert(new ERPatient("Number", "11", "Life-threatening"));
		// h.insert(new ERPatient("Number", "12", "Life-threatening"));
		// h.insert(new ERPatient("Number", "13", "Life-threatening"));
		// h.insert(new ERPatient("Number", "14", "Life-threatening"));
		// h.insert(new ERPatient("Number", "15", "Life-threatening"));

		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());
		// System.out.println(h.getRootItem());

		// System.out.println(h.size());
		// System.out.println(h.isEmpty());
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// h.getRootItem();
		// System.out.println(h.isEmpty());

		// int[] a = {6,1,2,7,12,63,51,19,18,92,48,13};
		// Heap<Integer> b = new Heap<Integer>();
		// b.getRootItem();
		// for (int i: a) {
		// 	b.insert(i);
		// }
		// while (!b.isEmpty()) {
		// 	System.out.println(b.getRootItem());
		// }
	}
}
