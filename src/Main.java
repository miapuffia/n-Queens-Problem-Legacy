package nQueensProblem.src;

import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import robertHelperFunctions.QuickAlert;

public class Main extends Application {
	private int boardSize = 0;
	GridPane boardGridPane;
	ArrayList<int[]> queensStack = new ArrayList<int[]>();
	int numQueens = 0;
	int squareSize = 50;
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		MenuItem menuItemSetN = new MenuItem();
		
		Menu menuSetN = new Menu("Set board size");
		menuSetN.getItems().addAll(menuItemSetN);
		menuSetN.addEventHandler(Menu.ON_SHOWN, event -> menuSetN.hide());
		menuSetN.addEventHandler(Menu.ON_SHOWING, event -> menuItemSetN.fire());
		
		menuItemSetN.setOnAction(e -> {
			Optional<String> result = QuickAlert.showNumericalInput("Container width:", "Set board size", boardSize + "");
			
			if(result.isPresent() && !result.get().equals("")) {
				int newSize = Integer.parseInt(result.get());
				if(newSize <= 13 && newSize >= 0) {
					boardSize = newSize;
				} else {
					QuickAlert.show(AlertType.WARNING, "Size is too big", "A grid size larger than 13 will take up at least 3 gigabytes of RAM and take quite a while to place queens. I don't think you want that.\nPlease use a grid size less than 13.");
				}
			}
			
			generateBoard();
		});
		
		MenuItem menuItemPlaceQueens = new MenuItem();
		
		Menu menuPlaceQueens = new Menu("Place queens");
		menuPlaceQueens.getItems().addAll(menuItemPlaceQueens);
		menuPlaceQueens.addEventHandler(Menu.ON_SHOWN, event -> menuPlaceQueens.hide());
		menuPlaceQueens.addEventHandler(Menu.ON_SHOWING, event -> placeQueens());
		
		MenuItem menuItemSetSquareSize = new MenuItem();
		
		Menu menuSetSquareSize = new Menu("Set square size");
		menuSetSquareSize.getItems().addAll(menuItemSetSquareSize);
		menuSetSquareSize.addEventHandler(Menu.ON_SHOWN, event -> menuSetSquareSize.hide());
		menuSetSquareSize.addEventHandler(Menu.ON_SHOWING, event -> menuItemSetSquareSize.fire());
		
		menuItemSetSquareSize.setOnAction(e -> {
			Optional<String> result = QuickAlert.showNumericalInput("Square size in pixels:", "Set square size", squareSize + "");
			
			if(result.isPresent() && !result.get().equals("")) {
				squareSize = Integer.parseInt(result.get());
			}
			
			generateBoard();
		});
		
		MenuBar menuBar = new MenuBar(menuSetN, menuPlaceQueens, menuSetSquareSize);
		
		boardGridPane = new GridPane();
		
		StackPane mainStackPane = new StackPane(boardGridPane);
		
		VBox mainVBox = new VBox(menuBar, mainStackPane);
		VBox.setVgrow(mainStackPane, Priority.ALWAYS);
		
		Scene scene = new Scene(mainVBox);
		
		generateBoard();
		
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setTitle("n-Queen Problem");
		primaryStage.show();
	}
	
	private void generateBoard() {
		boardGridPane.getChildren().clear();
		
		queensStack.clear();
		numQueens = 0;
		
		if(boardSize == 0) {
			boardGridPane.setBorder(null);
		} else {
			for(int i = 0; i < boardSize; i++) {
				for(int j = 0; j < boardSize; j++) {
					StackPane squareStackPane = new StackPane();
					Rectangle rectangle = null;
					
					if((i + j) % 2 == 0) {
						rectangle = new Rectangle(squareSize, squareSize, Color.WHITE);
					} else {
						rectangle = new Rectangle(squareSize, squareSize, Color.BLACK);
					}
					
					squareStackPane.getChildren().add(rectangle);
					
					boardGridPane.add(squareStackPane, i, j);
				}
			}
			
			boardGridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		}
		
		boardGridPane.setMaxSize(boardSize * squareSize, boardSize * squareSize);
	}
	
	private void placeQueens() {
		int requiredNumQueens = boardSize;
		
		if(boardSize == 3) {
			requiredNumQueens = 2;
		} else if(boardSize == 2) {
			requiredNumQueens = 1;
		}
		
		for(int i = 0; i < boardSize; i++) {
			if(checkForQueens(i, numQueens)) {
				addQueen(i, numQueens);
				numQueens++;
				
				if(numQueens != requiredNumQueens) {
					placeQueens();
				} else {
					return;
				}
			}
		}
		
		if(numQueens != requiredNumQueens) {
			numQueens--;
			removeQueen();
		}
	}
	
	private void addQueen(int x, int y) {
		queensStack.add(new int[] {x, y});
		
		ImageView queen = new ImageView("/nQueensProblem/resources/queen.png");
		queen.setFitWidth(squareSize - 2);
		queen.setFitHeight(squareSize - 2);
		
		ObservableList<Node> children = boardGridPane.getChildren();
		
		for(Node child : children) {
			if(GridPane.getRowIndex(child) == y && GridPane.getColumnIndex(child) == x) {
				if(((Rectangle) ((StackPane) child).getChildren().get(0)).getFill() == Color.WHITE) {
					ColorAdjust blackout = new ColorAdjust();
					blackout.setBrightness(-1.0);
					queen.setEffect(blackout);
				}
				
				((StackPane) child).getChildren().add(queen);
			}
		}
	}
	
	private boolean checkForQueens(int x, int y) {
		if(x == 0 && y == 0) {
			return true;
		}
		
		if(x >= boardSize || y >= boardSize) {
			return false;
		}
		
		for(int i = 0; i < queensStack.size(); i++) {
			if(queensStack.get(i)[0] == x || queensStack.get(i)[1] == y || Math.abs((double) (y - queensStack.get(i)[1]) / (x - queensStack.get(i)[0])) == 1) {
				return false;
			}
		}
		
		return true;
	}
	
	private void removeQueen() {
		if(queensStack.size() > 0) {
			int[] position = queensStack.get(queensStack.size() - 1);
			
			queensStack.remove(position);
			
			ObservableList<Node> children = boardGridPane.getChildren();
			
			for(Node child : children) {
				if(GridPane.getRowIndex(child) == position[1] && GridPane.getColumnIndex(child) == position[0]) {
					StackPane foundChild = (StackPane) child;
					
					if(foundChild.getChildren().size() == 2) {
						foundChild.getChildren().remove(1);
					}
				}
			}
		}
	}
}