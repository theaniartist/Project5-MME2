import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Pane;

/**
 * An abstract class that constructs a tree from the given list of expressions. Converts the expression into a String
 * and proceeds to flatten the tree if the parent node contains the same operator in the lext level of its children.
 */

public class ExpressionNode implements Expression
{
		
		private Label _label;
		protected HBox _horizontalBox = new HBox();
		private CompoundExpression _parent;
		private LinkedList<ExpressionNode> _children;
		
		public ExpressionNode(String data)
		{
			_label = new Label(data);
			_label.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 30));
			_parent = null;
			_children = new LinkedList<ExpressionNode>();
			_horizontalBox.getChildren().add(_label);
			//((Pane)_horizontalBox).setBorder(Expression.RED_BORDER);
		}
		
		/**
		 * @return Return the data associated with this ExpressionNode.
		 */
		public Label getLabel()
		{
			return _label;
		}
		
		public void setLabel(String str)
		{
			_label.setText(str);
		}
		
		/**
		 * 
		 * @return Return a linked list of children associated with an ExpressionNode.
		 */
		public LinkedList<ExpressionNode> getChildren()
		{
			return _children;
		}
		
		public Node getNode()
		{
			return _horizontalBox;
		}
		
		/**
		 * Method that recursively calls itself to create a String of type Expression and its children
		 * that starts at the given indent level.
		 * @param stringBuilder the stringBuilder that appends a name and a new line to create a String
		 * @param indentLevel the current indentation level
		 */
		public void convertToString(StringBuilder stringBuilder, int indentLevel)
		{
			
			indent(stringBuilder, indentLevel);

			stringBuilder.append(_label.getText());

			stringBuilder.append('\n');

			for (Expression expr : _children) {
				expr.convertToString(stringBuilder, indentLevel + 1);
			}
			
			System.out.println(stringBuilder.toString());
		}
		
		/**
		 * Helper method for indentation for StirngBuilder
		 * @param stringBuilder the stringBuilder that appends each tab character
		 * @param indentLevel the number of tabs that must be appended to StirngBuilder
		 */


		public static void indent(StringBuilder stringBuilder, int indentLevel) {

			for(int i = 0; i < indentLevel; i++) {

				stringBuilder.append('\t');

			}

		}

	/**
	 * Returns the expression's parent
	 * @return the expression's parent
	 */
		
		public CompoundExpression getParent()
		{
			return _parent;
		}

	/**
	 * Sets the parent to the specified parent expression
	 * @param parent the CompoundExpression that should be the parent of the target object
	 */

		public void setParent (CompoundExpression parent)
		{
			_parent = parent;
		}
		
		public double getGlobalCoordinate(BiFunction<Expression, Double, Double> callback, Expression expression, double value)
		{
			value = callback.apply(expression, value);
			if(expression.getParent() == null)
			{	
				return value;
			}
			else
			{
				return getGlobalCoordinate(callback, expression.getParent(), value);
			}
		}

	/**
	 * Method checks if what the user clicked on is within the bounding box of the expression that is shown on
	 * the window.
	 * @param mainPane the pane that displays the expression
	 * @param mouseX the x-coordinate of the mouse click
	 * @param mouseY the y-coordinate of the mouse click
	 * @return a boolean value that if the click coordinates is within the bounding box of the expression,
	 * then return true. If the clicj coordinates are not within the bounding box of the expression, return false.
	 *
	 */

		public boolean isClicked(Pane mainPane, double mouseX, double mouseY)
		{
			double hBoxX = getGlobalCoordinate((expression, value) -> value + expression.getNode().getLayoutX(), this, 0) + mainPane.getLayoutX();
			double hBoxY = getGlobalCoordinate((expression, value) -> value + expression.getNode().getLayoutY(), this, 0) + mainPane.getLayoutY();
			double hBoxX2 = hBoxX + _horizontalBox.getLayoutBounds().getWidth();
			double hBoxY2 = hBoxY + _horizontalBox.getLayoutBounds().getHeight();
			if(mouseX >= hBoxX && mouseX <= hBoxX2 && mouseY >= hBoxY && mouseY <= hBoxY2)
			{
				return true;
			}
			else
			{
				return false;
			}
		}

	/**
	 * Method takes in an expression and gets the index of another expression to "swap" its location with.
	 * The first part of the method swaps the expressions in the parsed expression tree by taking the indexes of
	 * both expressions and setting them to each other. In the second part of the method, it swaps the expressions'
	 * hboxes and setting them to each other.
	 * @param expression the expression that is going to be swapped with
	 */

	public void swapWith(Expression expression)
	{
		//Swap branches of the tree!
		int indexOfThis = ((ExpressionNode)_parent)._children.indexOf(this);
		int indexOfOther = ((ExpressionNode)_parent)._children.indexOf(expression);
		ExpressionNode placeHolder = ((ExpressionNode)_parent)._children.get(indexOfThis);
		((ExpressionNode)_parent)._children.set(indexOfThis, ((ExpressionNode)_parent)._children.get(indexOfOther));
		((ExpressionNode)_parent)._children.set(indexOfOther, placeHolder);
		
		//Swap HBoxes!
		//int indexOfParentLabel = ((HBox)_parent.getNode()).getChildren().indexOf(((ExpressionNode)_parent)._label);
		int indexOfThisHBox = ((HBox)_parent.getNode()).getChildren().indexOf(_horizontalBox);
		int indexOfOtherHBox = ((HBox)_parent.getNode()).getChildren().indexOf(expression.getNode());
		((HBox)_parent.getNode()).getChildren().set(indexOfOtherHBox, new Label());
		((HBox)_parent.getNode()).getChildren().set(indexOfThisHBox, expression.getNode());
		((HBox)_parent.getNode()).getChildren().set(indexOfOtherHBox, _horizontalBox);
	}
		
	/**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */

	public Expression deepCopy()
	{
		final Expression expressionCopy = new ExpressionNode(_label.getText());
		for (Expression expression : _children) {
			((ExpressionNode)expressionCopy)._children.add((ExpressionNode) expression.deepCopy());
			((HBox)expressionCopy.getNode()).getChildren().add(((ExpressionNode)expressionCopy)._children.getLast().getNode());
		}
		int indexOfLabel = _horizontalBox.getChildren().indexOf(_label);
		((HBox)expressionCopy.getNode()).getChildren().remove(((ExpressionNode)expressionCopy).getLabel());
		((HBox)expressionCopy.getNode()).getChildren().add(indexOfLabel,((ExpressionNode)expressionCopy).getLabel());
		return expressionCopy;
	}

	/**
	 * Recursively flattens the expression as much as possible
	 * throughout the entire tree. Specifically, in every multiplicative
	 * or additive expression x whose first or last
	 * child c is of the same type as x, the children of c will be added to x, and
	 * c itself will be removed. This method modifies the expression itself.
	 */

		public void flatten()
		{
			if (_children.size() > 0) 
			{
				int i = 0;
				while(i < _children.size())
				{
					ExpressionNode subExpr = _children.get(i);
					subExpr.flatten();
					if (_label.getText().equals(subExpr._label.getText())) 
					{
						for(Expression child : subExpr.getChildren())
						{
							child.setParent((CompoundExpression)this);
						}
						_children.addAll(0, subExpr._children);
						_horizontalBox.getChildren().addAll(0, ((HBox)subExpr.getNode()).getChildren());
						_horizontalBox.getChildren().remove(subExpr.getNode());
						_children.remove(subExpr);
					}
					i++;
				}				
			}
		}
}
