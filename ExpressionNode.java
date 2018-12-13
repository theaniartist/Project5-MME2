import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
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

		public boolean isClicked(double mouseX, double mouseY)
		{
			double hBoxX = _horizontalBox.getLayoutX() +  _horizontalBox.getTranslateX();
			double hBoxY = _horizontalBox.getLayoutY() +  _horizontalBox.getTranslateY();
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
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
		
		public Expression deepCopy()
		{
			return null;
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
						_children.addAll(0, subExpr._children);
						_children.remove(subExpr);
					}
					i++;
				}				
			}
		}
}
