package fr.istic.hanoi;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
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
    private Map<Integer, MyRectangle> discViews = new HashMap<>();
    private HanoiShape shape;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       
        Logger.getGlobal().info("Controller initialisation");

        //Create a model just for testing
        model = new HanoiModelImpl(3);
        shape = new HanoiShape(3, 600);
        Font textFont = new Font("Helvetica", 20);
        
        for(int i = 0; i<3; i++ ) {
        	Rectangle pilier = shape.getCylinder();
        	GridPane.setHalignment(pilier, HPos.CENTER);
        	GridPane.setValignment(pilier, VPos.TOP);
        	grid.addColumn(i, pilier);
        }
        
        for (Integer i : model.getStack(1)) {
            //Text t = new Text(i.toString());
            //t.setFont(textFont);
            //t.setTextAlignment(TextAlignment.CENTER);
        	System.out.println("value i : " + i);
        	MyRectangle rect = shape.getRectangles().get(i-1);
            discViews.put(i, rect);
            rect.setNumberDisk(i);
            GridPane.setHalignment(rect, HPos.CENTER);
            grid.add(rect, 0, i);
            rect.setOnDragDetected(de -> onDragDetected(de, rect));
            grid.setOnDragOver(this::onDragOver);
            grid.setOnDragDropped(this::onDragDropped);
            grid.setOnDragDone(this::onDragDone);
           // Logger.getGlobal().info(String.format("Added a new text %s"));
        }
       
        Logger.getGlobal().info(String.format("taille stack : " + model.getStack(1).size()));
       
    }
    
    private int getColonne(double x) {
    	double colIndex =  x / (grid.getWidth() / grid.getColumnCount());
        return (int) colIndex;
    }
    
    private void onDragDetected(MouseEvent mouseEvent, MyRectangle source) {
    	Dragboard dragBoard = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.put(MyCustomDataFormat.RECTANGLE, source);
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
        MyRectangle rect = (MyRectangle) db.getContent(MyCustomDataFormat.RECTANGLE);
        
        int colonneCible = getColonne(dragEvent.getX());
        int colonneSource = rect.getColonne();
  
    	if(model.isValidMove(colonneSource + 1, colonneCible + 1) && model.getStack(colonneSource+1).getFirst()== rect.getNumberDisk()) {
    		MyRectangle r = discViews.get(rect.getNumberDisk());
    		grid.getChildren().remove(r);
    		GridPane.setHalignment(r, HPos.CENTER);
    		grid.add(r, colonneCible, 3 - model.getStack(colonneCible + 1).size());
    		r.setColonne(colonneCible);
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