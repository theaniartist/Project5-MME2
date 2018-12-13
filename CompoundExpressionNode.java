import java.util.LinkedList;

import javafx.scene.control.Label;

/**
 * An abstract "helper" class for the operator classes to reduce the redundancy for similar methods.
 */

public class CompoundExpressionNode extends ExpressionNode implements CompoundExpression
{
	public CompoundExpressionNode(String data)
	{
		super(data);
	}

	/**
	 * Adds the passed in expression as a child
	 * @param expression the child expression that must be added
	 */
	
	public void addSubexpression(Expression expression)
	{
		getChildren().add((ExpressionNode)expression);
	}

	/**
	 * Gets the list of expressions
	 * @return the list of expressions
	 */
	
	public LinkedList<ExpressionNode> getSubexpressions()
	{
		return getChildren();
	}
}