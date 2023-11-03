package fr.istic.hanoi;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class HanoiController implements Initializable {
    @FXML
    private GridPane grid;

    @FXML
    protected void onFileNew() {
        Logger.getGlobal().info("Unimplemented");
    }

    private HanoiModel model;
    private Map<Integer, Text> discViews = new HashMap<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       
        Logger.getGlobal().info("Controller initialisation");

        //Create a model just for testing
        model = new HanoiModelImpl(3);
        Font textFont = new Font("Helvetica", 20);
        for (Integer i : model.getStack(1)) {
            Text t = new Text(i.toString());
            t.setFont(textFont);
            t.setTextAlignment(TextAlignment.CENTER);
            discViews.put(i, t);
            grid.add(t, 0, i - 1);
            t.setOnDragDetected(de -> onDragDetected(de, t));
            grid.setOnDragOver(this::onDragOver);
            grid.setOnDragDropped(this::onDragDropped);
            grid.setOnDragDone(this::onDragDone);
            Logger.getGlobal().info(String.format("Added a new text %s", t));
        }
        Logger.getGlobal().info(String.format("taille stack : " + model.getStack(1).size()));
       
    }
    
    private int getColonne(double x) {
    	double colIndex =  x / (grid.getWidth() / grid.getColumnCount());
        return (int) colIndex;
    }
    
    private void onDragDetected(MouseEvent mouseEvent, Text source) {
    	Dragboard dragBoard = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(source.getText());
        dragBoard.setContent(content);
        mouseEvent.consume();
    }
    
    private void onDragOver(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.MOVE);     // State that a drop is possible
        int colonne = getColonne(dragEvent.getX());

        Logger.getGlobal().info(String.format("L'événement de glissement a eu lieu dans la colonne : (" + colonne + ")"));
        dragEvent.consume();
    }
    
    private void onDragDone(DragEvent dragEvent) {
        if (dragEvent.getTransferMode() == TransferMode.MOVE) {
            dragEvent.getDragboard().clear();
        }
        dragEvent.consume();
    }
    
    private void onDragDropped(DragEvent dragEvent) {
    	Dragboard db = dragEvent.getDragboard();
        int data = Integer.parseInt(db.getString());
                
        int colonneCible = getColonne(dragEvent.getX());
        int colonneSource = getColonne(discViews.get(data).getLayoutX());
        
    	if(model.isValidMove(colonneSource + 1, colonneCible + 1) && model.getStack(colonneSource+1).getFirst()== data) {
    		Text text = discViews.get(data);
    		grid.getChildren().remove(text);
    		grid.add(text, colonneCible, 3 - model.getStack(colonneCible + 1).size());
    		try {
 				model.move(colonneSource + 1, colonneCible + 1);
			} catch (IllegalMoveException e) {
				System.err.println("catch : error move");
				e.printStackTrace();
			}
    	}
        dragEvent.setDropCompleted(true);
        dragEvent.consume();
    }
}