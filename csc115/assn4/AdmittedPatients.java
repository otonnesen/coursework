// Name: Oliver Tonnesen
// ID: V00885732
// Date: March 19, 2018
// Filename: AdmittedPatients.java
// Details: CSC 115 Assignment 4

public class AdmittedPatients {

	protected TreeNode root;

	// Constructor
	// Initializes empty BST
	
	AdmittedPatients() { this.root = null; }

	// Method:		admit
	// Description:		Enters the record of an admitted patient into the current BinarySearchTree, maintaining the ordering of the tree.
	// Input:
	//	patient    -    The patient that has been admitted.
	// Output:		None
	
	public void admit(HospitalPatient patient) {
		TreeNode n = new TreeNode(patient);
		if (this.root == null) {
			this.root = n;
		} else {
			insert(n, this.root);
		}
	}

	// Recursive helper method

	private void insert(TreeNode patient, TreeNode start) {
		int c = patient.item.compareTo(start.item);
		if (c < 0) {
			if (start.left == null) {
				start.left = patient;
			} else {
				insert(patient, start.left);
			}
		}
		if (c > 0) {
			if (start.right == null) {
				start.right = patient;
			} else {
				insert(patient, start.right);
			}
		} else {
			return;
		}
	}

	
	// Method:		getPatientInfo
	// Description:		Retrieves the information about a patient, given an id number.
	// Input:
	//	id	  -	The id of the patient
	// Output:		The complete record of that patient or null if the patient is not in hospital.
	
	public HospitalPatient getPatientInfo(String id) {
		TreeNode tmp = bSearch(id, this.root);
		if(tmp == null) {
			return null;
		}
		return tmp.item;
	}

	// Recursive helper method

	private TreeNode bSearch(String id, TreeNode start) {
		
		int c = id.compareTo(start.item.getId());
		if (c < 0) {
			if (start.left == null) {
				return null;
			}
			return bSearch(id, start.left);
		} else if (c > 0) {
			if (start.right == null) {
				System.out.println("r");
				return null;
			}
			return bSearch(id, start.right);
		} else {
			return start;
		}
	}

	// Method:		discharge
	// Description:		Removes a patient's record from the BinarySearchTree.
	// Input:
	//	patient   -	The patient record to remove.
	// Output:		None
	
	public void discharge(HospitalPatient patient) {
		this.root = del(this.root, patient.getId());
	}

	// Recursive helper method
	
	private TreeNode del(TreeNode start, String id) {
		if (start == null) { return start; }
		// Finding Node
		int c = id.compareTo(start.item.getId());
		if 	(c < 0) { start.left = del(start.left, id); }
		else if (c > 0) { start.right = del(start.right, id); }
		// Node found
		else { 
			if (start.left == null) {
				return start.right;
			} else if (start.right == null) {
				return start.left;
			} else {
				TreeNode t = start.right;
				while (t.left != null) {
					t = t.left;
				}
				// System.out.println(t.item.getId());
				start.right = del(start.right, t.item.getId());
				start.item = t.item;
			}
		}
		return start;
	}

	// Method:		printLocations
	// Description:		Prints out a list of patient locations in alphabetical order, one entry per line.
	// Input:		None
	// Output:		None

	public void printLocations() {
		inorder(this.root);
	}

	// Recursive helper method

	private void inorder(TreeNode start) {
		if(start == null) {
			return;
		}
		inorder(start.left);
		System.out.println(start.item.getLocation());
		inorder(start.right);
	}

	// Method:		main
	// Description:		Used for internal testing
	// Input:
	// 	args      -	Unused
	// Output:		None

	public static void main(String[] args) {
		// HospitalPatient a = new HospitalPatient(new SimpleDate(2000,1,1), "a", "A", 'A', 1);
		// HospitalPatient b = new HospitalPatient(new SimpleDate(2000,1,1), "b", "B", 'B', 1);
		// HospitalPatient c = new HospitalPatient(new SimpleDate(2000,1,1), "c", "C", 'C', 1);
		// HospitalPatient d = new HospitalPatient(new SimpleDate(2000,1,1), "d", "D", 'D', 1);
		// HospitalPatient e = new HospitalPatient(new SimpleDate(2000,1,1), "e", "E", 'E', 1);
		// HospitalPatient f = new HospitalPatient(new SimpleDate(2000,1,1), "f", "F", 'F', 1);
		// HospitalPatient g = new HospitalPatient(new SimpleDate(2000,1,1), "g", "G", 'G', 1);
		// HospitalPatient h = new HospitalPatient(new SimpleDate(2000,1,1), "h", "H", 'H', 1);
		// AdmittedPatients t = new AdmittedPatients();
		// t.admit(c);
		// t.admit(e);
		// t.admit(d);
		// t.admit(g);
		// t.admit(a);
		// t.admit(f);
		// t.admit(b);
		// t.admit(h);

		// System.out.println(t.getPatientInfo("a_0"));
		// System.out.println(t.getPatientInfo("a_1"));

		// t.printLocations();
		// ViewableTree v = new ViewableTree(t);
		// v.showFrame();
		// t.discharge(c);
		// t.printLocations();
		// ViewableTree v2 = new ViewableTree(t);
		// v2.showFrame();
		// System.out.println(a.getLocation());
		// System.out.println(b.getLocation());
		
		// TreeNode t2 = 		new TreeNode(new HospitalPatient(new SimpleDate(2000,1,1), "a", "", ' ', 1),
		// 				new TreeNode(new HospitalPatient(new SimpleDate(2000,1,1), "b", "", ' ', 1)),
		// 				new TreeNode(new HospitalPatient(new SimpleDate(2000,1,1), "c", "", ' ', 1))
		// 				);
		// t.inorder(t2);

		// System.out.println(a.getId());
		// System.out.println(b.getId());
		// System.out.println(t.getPatientInfo("a_0"));
		// System.out.println(t.getPatientInfo("b_1"));
		
	}

}
