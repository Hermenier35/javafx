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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private BorderPane monBorderPane;

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
    private ImageView zinzinView;
    private SequentialTransition animationFirstPilier;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
    	StackPane gameView = new StackPane();
    	Image zinzin = new Image(System.getProperty("user.dir") + "\\src\\main\\resources\\fr\\istic\\images\\zinzin.png");
    	this.zinzinView = new ImageView(zinzin);
    	//gameView.getChildren().addAll(monBorderPane, this.zinzinView);
    	monBorderPane.getChildren().add(zinzinView);
    	zinzinView.setFitWidth(200);  // Largeur souhaitée
        zinzinView.setFitHeight(150);
        zinzinView.setTranslateY(20);
                
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
        Image image = null;
        String relativePath = "\\src\\main\\resources\\fr\\istic\\images\\fond.png";
        String absolutePath = System.getProperty("user.dir") + relativePath;
        System.out.println(absolutePath);
		try {
			FileInputStream in = new FileInputStream(absolutePath);
			image = new Image(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        if (image.isError()) {
            System.out.println("Erreur lors du chargement de l'image");
        } else {
            System.out.println("Image chargée avec succès");
        }
        BackgroundSize size = new BackgroundSize(600, 400, false, false, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size
        );
        Background background = new Background(backgroundImage);
        grid.setBackground(background);
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
        if(piliers.get(2).getChildren().size()-1 == this.numberOfDisks) {
        	animationZinzinGoToLastPilier();
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
    		Rectangle pilier = (Rectangle)piliers.get(colonneCible).getChildren().get(0);
            r.setTranslateY(-pilier.getHeight());
    		piliers.get(colonneSource).getChildren().remove(r);
    		piliers.get(colonneCible).getChildren().add(r);
    		animation(r,piliers.get(colonneCible), pilier.getHeight());
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
        	pilier.setTranslateY(10);
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
              
        this.animationFirstPilier = animationZinzinOnFirstPilier();
        
        Logger.getGlobal().info(String.format("taille stack : " + model.getStack(1).size()));

    }
    
    private void animation(MyRectangle disk, StackPane pilierTatget, double hauteurPilier) {
    	int hauteur = pilierTatget.getChildren().size()-2;
    	TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), disk);
        translateTransition.setByY(hauteurPilier - (hauteur * heightDisk));
        translateTransition.setCycleCount(1);
        translateTransition.play();
    }
    
    private SequentialTransition animationZinzinOnFirstPilier() {
    	Rectangle firstPilier = (Rectangle)piliers.get(0).getChildren().get(0);
    	zinzinView.xProperty().bind(Bindings.createDoubleBinding(()-> firstPilier.getLayoutX() - 90, firstPilier.layoutXProperty()));
    	zinzinView.yProperty().bind(Bindings.createDoubleBinding(()-> firstPilier.getLayoutY() - 200, firstPilier.layoutXProperty()));
    	TranslateTransition translateDownTransition = new TranslateTransition(Duration.seconds(1), this.zinzinView);
    	TranslateTransition translateUpTransition = new TranslateTransition(Duration.seconds(1), this.zinzinView);
        translateDownTransition.setByY(10);
        translateUpTransition.setByY(-10);
        SequentialTransition bounceTransition = new SequentialTransition(translateDownTransition, translateUpTransition);
        bounceTransition.setCycleCount(Animation.INDEFINITE);
        bounceTransition.play();
        return bounceTransition;
    }
    
    private void animationZinzinGoToLastPilier() {
    	Rectangle lastPilier = (Rectangle)piliers.get(2).getChildren().get(0);
    	TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), this.zinzinView);
    	translateTransition.setToX(lastPilier.getParent().getLayoutX());
    	System.out.println(lastPilier.getParent().getLayoutX());
    	translateTransition.setCycleCount(1);
    	translateTransition.play();
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