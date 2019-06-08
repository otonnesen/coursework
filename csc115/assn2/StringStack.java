public class StringStack {

	private Node top;

	// Constructor
	// Description:		Initializes a stack that has no elements.
	// Input:		None
	// Throws:		None

	public StringStack() { this.top = null; }

	// Method:		isEmpty
	// Description:		Determines whether or not a stack is empty.
	// Input:		None
	// Output:		True if the stack is empty, false if not.

	public boolean isEmpty() {
		if(this.top == null) {
			return true;
		}
		return false;
	}

	// Method:		push
	// Description:		Inserts an element onto the top of the stack.
	// Input:
	// 	s - The element
	// Output:		None

	public void push(String s) {
		this.top = new Node(s, this.top);
	}

	// Method:		pop
	// Description:		Removes and returns the top element of the stack 
	// Input:		None
	// Output:		The top element
	// Throws:		StackException - if the stack is empty.

	public String pop() throws StackException {
		if(this.isEmpty()) {
			throw new StackException();
		}
		Node temp = this.top;
		this.top = this.top.next;
		return temp.item;
	}

	// Method:		peek
	// Description:		Returns but does not remove the top element of the stack.
	// Input:		None
	// Output:		The top element
	// Throws:		StackException - if the stack is empty.

	public String peek() throws StackException {
		if(this.isEmpty()) {
			throw new StackException();
		}
		return this.top.item;
	}

	// Method:		popAll
	// Description:		Empties all the elements from the stack.
	// Input:		None
	// Output:		None

	public void popAll() {
		this.top = null;
	}

}
