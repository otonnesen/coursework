public class Evaluator {

	// Constructor
	// Description:		Initializes an evaluator object.
	// Input:		None
	// Throws:		None

	public Evaluator() {

	}

	// Method:		main
	// Description:		Used for testing purposes only.
	// Input:
	// 	args - 		unused
	// Output:		None

	public static void main(String[] args) {
		System.out.println(evaluate("(25-16)*4-9*(3+2)"));
	}

	// Method:		evaluate
	// Description:		Solves an arithmetic expression.
	// Input:
	// 	expression - 	An arithmetic expression with numbers and the operators defined in the Operator class.
	// Output:		The numeric value of the expression.
	// Throws:
	// 	IllegalExpressionException - if the expression cannot be solved.

	public static double evaluate(String expression) throws IllegalExpressionException {
		PostfixTokenizer p = new PostfixTokenizer(new OperatorTokenizer(expression));
		StringStack s = new StringStack();
		String token;
		Double op1;
		Double op2;

		while(p.hasNext()) {
			token = p.next();
			if(!Operator.isOperator(token)) {
				s.push(token);
			} else {
				try {
					op2 = Double.parseDouble(s.pop());
					op1 = Double.parseDouble(s.pop());
				} catch (NumberFormatException e) {
					throw new IllegalExpressionException("Invalid operands");
				} catch (StackException e) {
					throw new IllegalExpressionException("Not enough operands");
				}
				s.push(Double.toString(Operator.evaluate(op1, op2, token)));
			}
		}
		double result = Double.parseDouble(s.pop());

		if(!s.isEmpty()) {
			throw new IllegalExpressionException("Too many operands");
		}

		return result;
	}

}
