import java.util.NoSuchElementException;

public class PostfixTokenizer implements Tokenizer {

	private String[] tokens;
	private int currTokenIndex;

	// Constructor
	// Description:		Creates a new postfix order Tokenizer. The ordering of the new tokenizer
	// 			is postfix, meaning that the arithmetic operators are arranged in a postfix order.
	// 			If all the non-operators and non-parens are valid operands, the result will be a
	// 			valid postfix arithmetic expression, with only operands and operators -- no parens.
	// Input:
	// 	infixTokens			- A tokenizer of infix-ordered operands, which may be any string, operators and parens.
	// Throws:
	// 	IllegalExpressionException	- if the parens are unbalanced. Does not check that the operands are valid variable
	// 					names or numbers.
	
	public PostfixTokenizer(OperatorTokenizer infixToken) throws IllegalExpressionException {
		this.tokens = new String[infixToken.numTokens()-countBraces(infixToken)];
		this.currTokenIndex = 0;
		// if(!this.hasBalancedBraces(infixToken)) { throw new IllegalExpressionException(); }
		parse(infixToken);
	}

	private int countBraces(OperatorTokenizer input) {
		int counter = 0;
		String token;
		while(input.hasNext()) {
			token = input.next();
			if(token.equals("(") || token.equals(")")) {
				counter++;
			}
		}
		return counter;
	}

	private void parse(OperatorTokenizer input) throws IllegalExpressionException {
		input.reset();
		StringStack opStack = new StringStack();

		String token;
		int i = 0;
		while(input.hasNext()) {
			token = input.next();
			try {
				// If operand, add to next spot on tokens
				Double.parseDouble(token);
				this.tokens[i++] = token;
			} catch (NumberFormatException e) {
				if(token.equals("(")) { opStack.push(token); } // If '(', push to opStack
				else if(token.equals(")")) { // If ')', pop opStack onto tokens until '(' is removed
					while(!opStack.peek().equals("(")) {
						this.tokens[i++] = opStack.pop();
						if(opStack.isEmpty()) { throw new IllegalExpressionException("Mismatched parentheses"); } // Mismatched parentheses
					}
					opStack.pop();
				} else if(Operator.isOperator(token)) { // If operator, push onto opStack after popping any operators of equal or greater precedence to tokens
					while(!opStack.isEmpty() && Operator.isOperator(opStack.peek())) {
						if(Operator.comparePrecedence(token, opStack.peek()) >= 0) {
							this.tokens[i++] = opStack.pop();
						} else {
							break;
						}
					}
					opStack.push(token);
				} else {
					throw new IllegalExpressionException("Expression contains a non-operator, non-operand, non-parenthetical character");
				}

			}
		}
		while(!opStack.isEmpty()) { // Pop remainng operators to tokens
			if(!Operator.isOperator(opStack.peek())) { throw new IllegalExpressionException("Mismatched parentheses"); } // Mismatched parentheses
			this.tokens[i++] = opStack.pop();
		}

	
	}

	// Helper method
	// No longer needed
/*
	private boolean hasBalancedBraces(OperatorTokenizer input) {
		StringStack s = new StringStack();
		String token;
		while(input.hasNext()) {
			token = input.next();
			if(token.equals("(")) {
				s.push(token);
			} else if(token.equals(")")) {
				if(s.isEmpty()) { return false; }
				else { s.pop(); }
			}
		}

		return true;

	}
*/

	// Method:		main
	// Description:		Used for internal testing
	// Input:
	// 	args		- Unused
	// Output:		None

	public static void main(String[] args) {
		/*
		OperatorTokenizer o = new OperatorTokenizer("(25-16)*4-9*(3+2)");
		PostfixTokenizer p = new PostfixTokenizer(o);
		System.out.println(p);
		*/

		/*
		OperatorTokenizer o2 = new OperatorTokenizer("(25-)*4-9*(3+2)");
		PostfixTokenizer p2 = new PostfixTokenizer(o2);
		System.out.println(p2);
		*/

		/*
		String asString = "3*(4+-2)";
		OperatorTokenizer asInfix = new OperatorTokenizer(asString);
		PostfixTokenizer postfix = new PostfixTokenizer(asInfix);
		System.out.print("as infix tokens: ");
		System.out.println(asInfix);
		System.out.println("By individual postfix tokens:");
		while(postfix.hasNext()) {
			System.out.println("next token: "+postfix.next());
		}
		*/
	}

	// Method:		reset
	// Description:		Sets the iterator start position for the first item in the list.
	// Input:		None
	// Output:		None

	public void reset() {
		this.currTokenIndex = 0;
	}

	// Method:		numTokens
	// Description:		Returns number of tokens in the list.
	// Input:		None
	// Output:		The number of tokens in the list.

	public int numTokens() {
		return this.tokens.length;
	}

	// Method:		toString
	// Description:		The format is a list of tokens on a single line, delimited by a single space.
	// Input:		None
	// Output:		The formatted string.

	public String toString() {
		StringBuilder s = new StringBuilder();
		for(String i: tokens) {
			s.append(i+" ");
		}
		return s.substring(0, s.length());
	}

	/* Iterator required methods */

	public boolean hasNext() {
		return currTokenIndex != tokens.length;
	}

	public String next() {
		if(!this.hasNext()) {
			throw new NoSuchElementException("No more tokens");
		}
		return tokens[currTokenIndex++];
	}

	public void remove() {
		throw new UnsupportedOperationException("Tokens may not be removed");
	}

	/* End of iterator required methods */

}
