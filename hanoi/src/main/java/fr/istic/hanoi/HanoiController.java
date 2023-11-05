package fr.istic.hanoi;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class HanoiController implements Initializable {
    @FXML
    private GridPane grid;
    
    @FXML
    private Label score, nbr;
    
    @FXML
    private MenuItem menuItemNew, menuItemClose;
    
    @FXML
    private MenuItem easy, medium, hard;

    @FXML
    protected void onFileNew() {
        Logger.getGlobal().info("Unimplemented");
    }

    private HanoiModel model;
    private Map<Integer, MyRectangle> discViews = new HashMap<>();
    private ArrayList<StackPane> piliers = new ArrayList<>();
    private int heightDisk;
    private int numberOfDisks;
    private HanoiShape shape;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       
        Logger.getGlobal().info("Controller initialisation");
        Font customFont = Font.font("Minecraft",FontWeight.BOLD ,20);
        score.setFont(customFont);
        nbr.setFont(customFont);
        HBox hbox = (HBox) score.getParent();
        hbox.setAlignment(Pos.CENTER);
        menuItemNew.setOnAction(this::handleMenuItemNewClick);
        menuItemClose.setOnAction(this::handleMenuItemCloseClick);
        easy.setOnAction(this::handleMenuItemEasyClick);
        medium.setOnAction(this::handleMenuItemMediumClick);
        hard.setOnAction(this::handleMenuItemHardClick);
        this.numberOfDisks = 3;
        
        initializeGame();
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
        //Mode Debug
        int colonne = getColonne(dragEvent.getX());
        for(int i = 0; i < 3; i++) {
        	Rectangle pilier = (Rectangle) piliers.get(i).getChildren().get(0);
        	if(i==colonne)
        		pilier.setFill(shape.getLinearGradient());
        	else
        		pilier.setFill(Color.BROWN);
        }
        //Logger.getGlobal().info(String.format("L'événement de glissement a eu lieu dans la colonne : " + colonne));
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
        int indexFirstDisk = piliers.get(colonneSource).getChildren().size()-1;
        MyRectangle firstDisk = (MyRectangle)piliers.get(colonneSource).getChildren().get(indexFirstDisk);
    	if(model.isValidMove(colonneSource + 1, colonneCible + 1) && firstDisk.getNumberDisk()== rect.getNumberDisk()) {
    		MyRectangle r = discViews.get(rect.getNumberDisk());
    		int n = piliers.get(colonneCible).getChildren().size()-1;
            r.setTranslateY(n * (-heightDisk));
    		piliers.get(colonneSource).getChildren().remove(r);
    		piliers.get(colonneCible).getChildren().add(r);
    		r.setColonne(colonneCible);
    		nbr.setText(String.valueOf(Integer.parseInt(nbr.getText())+1));
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
    
    private void initializeGame() {
    	discViews.clear();
    	piliers.clear();
    	grid.getChildren().clear();
    	nbr.setText(0+"");
        this.heightDisk = 30;
        model = new HanoiModelImpl(numberOfDisks);
        this.shape = new HanoiShape(numberOfDisks, 600, heightDisk);
        
        for(int i = 0; i < 3; i++ ) {
        	StackPane stack = new StackPane();
        	Rectangle pilier = shape.getCylinder();
        	stack.getChildren().add(pilier);
        	stack.setAlignment(Pos.BOTTOM_CENTER);
        	piliers.add(stack);
        	grid.addColumn(i, stack);
        }
        
        
        StackPane firstPilier = piliers.get(0);
        for (Integer i : model.getStack(1)) {
        	MyRectangle rect = shape.getRectangles().get(i-1);
            discViews.put(i, rect);
            rect.setNumberDisk(i);
            int n = firstPilier.getChildren().size() - 1;
            rect.setTranslateY(n * (-heightDisk));
            firstPilier.getChildren().add(rect);
            rect.setOnDragDetected(de -> onDragDetected(de, rect));
            grid.setOnDragOver(this::onDragOver);
            grid.setOnDragDropped(this::onDragDropped);
            grid.setOnDragDone(this::onDragDone);
        }
              
       
        Logger.getGlobal().info(String.format("taille stack : " + model.getStack(1).size()));

    }
    
    private void handleMenuItemNewClick(ActionEvent event) {
    	initializeGame();
    }
    
    private void handleMenuItemCloseClick(ActionEvent event) {
    	Platform.exit();
    }
    
    private void handleMenuItemEasyClick(ActionEvent event) {
    	this.numberOfDisks = 3;
    	initializeGame();
    }
    
    private void handleMenuItemMediumClick(ActionEvent event) {
    	this.numberOfDisks = 4;
    	initializeGame();
    }
    
    private void handleMenuItemHardClick(ActionEvent event) {
    	this.numberOfDisks = 6;
    	initializeGame();
    }
}