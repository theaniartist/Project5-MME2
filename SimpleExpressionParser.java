import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Function;
import javafx.scene.layout.HBox;

/**
 * Starter code to implement an ExpressionParser. Your parser methods should use the following grammar:
 * E := A | X
 * A := A+M | M
 * M := M*M | X
 * X := (E) | L
 * L := [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {
	/**
	 * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
	 * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @param withJavaFXControls you can just ignore this variable for R1
	 * @return the Expression object representing the parsed expression tree
	 */
	public Expression parse (String str, boolean withJavaFXControls) throws ExpressionParseException {
		// Remove spaces -- this simplifies the parsing logic
		str = str.replaceAll(" ", "");
		Expression expression = parseExpression(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		return expression;
	}

	/**
	 * Checks if the expression being passed in is a valid expression that can be parsed.
	 * @param str the String representation of the expression
	 * @param operator the character representation of the operator that may be passed in (+,*,(), or a literal)
	 * @param testLeftSubexpression function that tests for the left part of the expression (before the operator)
	 * @param testRightSubexpression function that tests for the right part of the expression (after the operator)
	 * @return a boolean value of whether or not the expression that is being passed in is valid to parse
	 */
	
	private static CompoundExpression parseOperator(String str, char operator, Function<String, Expression> parseLeftSubexpression, Function<String, Expression> parseRightSubexpression)
	{		
		for(int i = 1; i < str.length() - 1; i++)
		{
			if(str.charAt(i) == operator)
			{
				Expression leftSubexpression = parseLeftSubexpression.apply(str.substring(0, i));
				Expression rightSubexpression = parseRightSubexpression.apply(str.substring(i + 1));
				if(leftSubexpression != null && rightSubexpression != null)
				{
					CompoundExpression compoundExpression = new CompoundExpressionNode("" + operator);
					compoundExpression.addSubexpression(leftSubexpression);
					leftSubexpression.setParent(compoundExpression);
					((HBox)compoundExpression.getNode()).getChildren().add(0, leftSubexpression.getNode());
					compoundExpression.addSubexpression(rightSubexpression);
					rightSubexpression.setParent(compoundExpression);
					((HBox)compoundExpression.getNode()).getChildren().add(rightSubexpression.getNode());
					return compoundExpression;
				}
			}
		}
		return null;
	}

	/**
	 * Parses the expression by the production rule of E: checks if the subexpression contains either
	 * terminal A or M from the boolean methods (isA() or isM()).
	 * @param str the String representation of the expression
	 * @return the expression that is either being parsed through the production rule of A or M. If
	 * the expression cannot be parsed by production rules of A or M, it would return null.
	 */
	
	private static int findEndOfParenthetical(String str)
	{
		char [] charArray = str.toCharArray();
		int occurencesOfOpenParentheses = Utility.numberOfOccurences(str, '(');
		int occurencesOfClosedParentheses = Utility.numberOfOccurences(str, ')');
		if(occurencesOfOpenParentheses == occurencesOfClosedParentheses)
		{
			int countClosedParentheses = 0;
			for(int i = 0; i < charArray.length; i++)
			{
				if(charArray[i] == ')')
				{
					countClosedParentheses++;
					if(countClosedParentheses == occurencesOfClosedParentheses)
					{
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * Parses the expression through the production rule E.
	 * @param str the expression that is either being parsed through the production rule A or X.
	 * @return the left and right side expression of the operator. Or returns the parsed expression from the
	 * production rule of X. If it does not parse through the production rule, return null.
	 */
	private static Expression parseE(String str)
	{
		Expression additiveExpression = parseA(str);
		Expression parentheticalNode = parseX(str);
		if(additiveExpression != null)
		{
			return additiveExpression;
		}
		else if(parentheticalNode != null)
		{
			return parentheticalNode;
		}
		else
		{
			return null;	
		}
	}

	/**
	 * Parses the expression through the production rule A. Checks if the expression contains the operator '+'
	 * in the expression.
	 * @param str the expression that is either being parsed through the condition of A+M or by the production
	 * rule of M.
	 * @return the left and right side expression of the operator. Or returns the parsed expression from the
	 * production rule of M. If it does not parse through the production rule, return null.
	 */
	private static Expression parseA(String str)
	{
		Expression additiveExpression = parseOperator(str, '+', SimpleExpressionParser::parseA, SimpleExpressionParser::parseM);
		Expression multiplicativeExpression = parseM(str);
		if(additiveExpression != null)
		{
			return additiveExpression;
		}
		else if(multiplicativeExpression != null)
		{
			return multiplicativeExpression;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Parses the expression through the production rule M. Checks if the expression contains the operator '*'
	 * in the expression.
	 * @param str the expression that is either being parsed through the condition of M*M or by the production
	 * rule of X.
	 * @return the left and right side expression of the operator. Or returns the parsed expression from the
	 * production rule of X. If it does not parse through the production rule, return null.
	 */

	private static Expression parseM(String str)
	{
		Expression multiplicativeExpression = parseOperator(str, '*', SimpleExpressionParser::parseM, SimpleExpressionParser::parseM);
		Expression parentheticalExpression = parseX(str);
		if(multiplicativeExpression != null)
		{
			return multiplicativeExpression;
		} 
		else if(parentheticalExpression != null)
		{
			return parentheticalExpression;
		}
		else
		{
			return null;	
		}
	}

	/**
	 * Parses the expression through the production rule X. Checks if the expression contains the '(' and ')'
	 * in the expression.
	 * @param str the expression that is either being parsed through the condition of (E) or by the production
	 * rule of L.
	 * @return the left and right side expression of the operator. Or returns the parsed expression from the
	 * production rule of L. If it does not parse through the production rule, return null.
	 */

	private static Expression parseX(String str)
	{
		if(str.charAt(0) == '(')
		{
			int closeParenthesisIndex = findEndOfParenthetical(str);
			if(closeParenthesisIndex != -1)
			{
				Expression innerExpression = parseE(str.substring(1, closeParenthesisIndex));
				if(innerExpression != null)
				{
					ParentheticalExpression parentheticalNode = new ParentheticalExpression("()");
					parentheticalNode.addSubexpression(innerExpression);
					innerExpression.setParent(parentheticalNode);
					parentheticalNode.setLabel("(" + str.substring(1, closeParenthesisIndex) + ")");
					CompoundExpressionNode nonTerminalNode;
					if(closeParenthesisIndex + 1 != str.toCharArray().length)
					{
						if(str.charAt(closeParenthesisIndex + 1) == '+' || str.charAt(closeParenthesisIndex + 1) == '*')
						{
							nonTerminalNode = new CompoundExpressionNode("" + str.charAt(closeParenthesisIndex + 1));
							Expression restOfExpression = parseE(str.substring(closeParenthesisIndex + 2));
							nonTerminalNode.addSubexpression(parentheticalNode);
							parentheticalNode.setParent(nonTerminalNode);
							((HBox)nonTerminalNode.getNode()).getChildren().add(0, parentheticalNode.getNode());
							nonTerminalNode.addSubexpression(restOfExpression);
							restOfExpression.setParent(nonTerminalNode);
							((HBox)nonTerminalNode.getNode()).getChildren().add(restOfExpression.getNode());
							return nonTerminalNode;
						}
					}
					return parentheticalNode;
				}
			}
			return null;
		}
		else
		{
			return parseL(str);
		}
	}

	/**
	 * Method checks for the production rule of L: whether the expression contains a literal
	 * case of numbers or letters (from a-z). Also checks for whether the expression being passed
	 * in contains any of the array of characters that should not be considered as a literal.
	 * @param str the expression that is being checked for the production rule of L.
	 * @return a boolean value whether it does or does not pass the test for the production rule of L
	 */
	
	private static Expression parseL(String str)
	{
		char [] unallowedCharacters = {'*', '+', '~', '`', '!', '@', '#', '$', '%', '^', '&', '(', ')', '-', '_', '=', '{', '[', '}', ']', ':', ';', '"', '\\', '\'', '?',
										'/', '>', '.', '<', ',', '|'}; 
		for(int i = 0; i < unallowedCharacters.length; i++)
		{
			for(int j = 0; j < str.toCharArray().length; j++)
			{
				if(str.toCharArray()[j] == unallowedCharacters[i])
				{
					return null;
				}
			}
		}
		return new LiteralExpression(str);
	}

	/**
	 * Checks if the expression being passed in is a valid expression that can be parsed.
	 * @param str the String representation of the expression
	 * @param operator the character representation of the operator that may be passed in (+,*,(), or a literal)
	 * @param testLeftSubexpression function that tests for the left part of the expression (before the operator)
	 * @param testRightSubexpression function that tests for the right part of the expression (after the operator)
	 * @return a boolean value of whether or not the expression that is being passed in is valid to parse
	 */

	/*
	private static boolean isValidExpression(String str)
	{
		int occurencesOfOpenParentheses = Utility.numberOfOccurences(str, '(');
		int occurencesOfClosedParentheses = Utility.numberOfOccurences(str, ')');
		char [] base10Digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		
		
		if(str.charAt(0) == ')' || str.charAt(0) == '+' || str.charAt(0) == '*')
		{
			return false;
		}
		else if(str.charAt(str.length() - 1) == '(' || str.charAt(str.length() - 1) == '+' || str.charAt(str.length() - 1) == '*')
		{
			return false;
		}
		else if(occurencesOfOpenParentheses != occurencesOfClosedParentheses)
		{
			return false;
		}
		else 
		{
			for(int i = 0; i < str.length(); i++)
			{
				for(int j = 0; j < base10Digits.length; j++)
				{	
					if(str.charAt(i) == base10Digits[j])
					{
						if(i + 1 != str.length() && str.charAt(i+1) == '(')
						{
							return false;
						}
						if(i - 1 > str.length() && str.charAt(i-1) == ')')
						{
							return false;
						}
					}
				}
				if(str.charAt(i) == '(')
				{
					
				}
			}
		}
	}
	 */
	/**
	 * Method parses the String representation of the expression that is being passed in. Parses through the
	 * first production rule: E, to check if it either contains the additive case (A+M) or contains an expression
	 * that can be parsed through the production rule of M
	 * @param str the String representation of the expression that is going to be parsed
	 * @return the parsed expression from the production rule methods
	 */

	protected Expression parseExpression (String str) {
		return parseE(str); 
	}

}
