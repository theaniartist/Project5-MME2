import javafx.application.Application;
import java.util.*;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
		
		Pane _pane;
		Expression _rootExpression;
		Expression _expressionOfFocus;
		
		MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
			_pane = pane_;
			_rootExpression = rootExpression_;
			_expressionOfFocus = rootExpression_;
		}


		/**
		 * Method handles a mouse event (either the mouse was pressed, dragged, or released) and
		 * initiates a specific action once the user has done a mouse event. If the user clicks
		 * on the expression, it initiated the MOUSE_PRESSED event which would set a red border around
		 * that sub-expression that the user clicked on. If the user holds down on the mouse, it intiates
		 * the MOUSE_DRAGGED event in which the user is able to drag the sub-expression. If the user releases
		 * the mouse, it initiates the MOUSE_RELEASED event which would set the sub-expression to where it was
		 * dragged and reset the translated x and y back to being 0.
		 *
		 * @param event the type of mouse event that responds to the user's actions
		 */

		public void handle (MouseEvent event) {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				double mouseX = event.getSceneX();
				double mouseY = event.getSceneY();
				boolean clicked = false;
				for(ExpressionNode subExpr : ((ExpressionNode)_expressionOfFocus).getChildren())
				{
					if(subExpr.isClicked(_pane, mouseX, mouseY))
					{
						((Pane) _expressionOfFocus.getNode()).setBorder(Expression.NO_BORDER);
						_expressionOfFocus = subExpr;
						((Pane) _expressionOfFocus.getNode()).setBorder(Expression.RED_BORDER);
						clicked = true;
					}
				}
				if(!clicked)
				{
					((Pane) _expressionOfFocus.getNode()).setBorder(Expression.NO_BORDER);
					_expressionOfFocus = _rootExpression;
				}
				//System.out.println(event.getSceneX());
				//System.out.println(event.getSceneY());
			} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			}
		}
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed	
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle (MouseEvent e) {
				// Try to parse the expression
				try {
					// Success! Add the expression's Node to the expressionPane
					final Expression expression = expressionParser.parse(textField.getText(), true);
					System.out.println(expression.convertToString(0));
					expressionPane.getChildren().clear();
					expressionPane.getChildren().add(expression.getNode());
					expression.getNode().setLayoutX(32);
					expression.getNode().setLayoutY(WINDOW_HEIGHT/3);

					// If the parsed expression is a CompoundExpression, then register some callbacks
					if (expression instanceof CompoundExpression) {
						((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
						final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (CompoundExpression) expression);
						expressionPane.setOnMousePressed(eventHandler);
						expressionPane.setOnMouseDragged(eventHandler);
						expressionPane.setOnMouseReleased(eventHandler);
					}
				} catch (ExpressionParseException epe) {
					// If we can't parse the expression, then mark it in red
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
}
