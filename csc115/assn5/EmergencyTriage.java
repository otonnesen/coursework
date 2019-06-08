// Name: Oliver Tonnesen
// ID: V00885732
// Date: March 30, 2018
// Filename: EmergencyTriage.java
// Details: CSC 115 Assignment 5

/**
 * EmergencyTriage.java
 * Provided the necessary information here,
 * making sure to delete these lines.
 */

// This is the beginning of the source code to help you get started.
// Do not change the following:

public class EmergencyTriage {
	private Heap<ERPatient> waiting;

	/**
	 * The empty EmergencyTriage is initialized.
	 */
	public EmergencyTriage() {
		waiting = new Heap<>();
	}

	// Method:		admitToER
	// Description:		Removes the highest priority patient from
	// 			    triage to enter the Emergency Room.
	// Input:		None
	// Output:		The record of the patient that is moving
	// 			    into the Emergency Room.

	public ERPatient admitToER() {
		return this.waiting.getRootItem();
	}

	// Method:		numberOfPatientsWaiting
	// Description:		Finds how many patients have been registered
	// 			    into triage but not yet admitted to the
	// 			    Emergency Room.
	// Input:		None
	// Output:		The number of patients who have been registered
	// 			    into triage but not yet admitted to the
	// 			    Emergency Room.

	public int numberOfPatientsWaiting() {
		return this.waiting.size();
	}

	// Method:		register
	// Description:		A patient is registed at triage.
	// 			The triage nurse creates a record
	// 			for the patient and the patient is then
	// 			added to the triage queue.
	// Input:
	//   lastName       -	The patient's last name.
	//   firstName      -	The patient's first name.
	//   triageCategory -	The triage category assigned to the
	//   			    patient by the triage nurse.
	// Output:		None

	public void register(String lastName,
			     String firstName,
			     String triageCategory) {
		this.waiting.insert(
			new ERPatient(lastName, firstName, triageCategory));
	}

	// Method:		whoIsNext
	// Description:		Retrieves the record of the patient who
	// 			    currently has the highest priority.
	// 			    The patient's record remains in the queue.
	// Input:		None
	// Output:		The record of the patient.

	public ERPatient whoIsNext() {
		return null;
	}

	// Method:		main
	// Description:		Used for internal testing only.
	// Input:		
	// 	args    -	Not used.
	// Output:		None

	public static void main(String[] args) {
		EmergencyTriage t = new EmergencyTriage();
		t.register("Number", "7", "Ambulatory");
		t.register("Number", "1", "Life-threatening");
		t.register("Number", "3", "Major fracture");
		t.register("Number", "4", "Acute");
		t.register("Number", "2", "Life-threatening");
		t.register("Number", "5", "Chronic");
		t.register("Number", "6", "Chronic");
		System.out.println(t.numberOfPatientsWaiting());
		for (int i = 0; i < 7; i++) {
			System.out.println(t.admitToER());
		}
	}
}
