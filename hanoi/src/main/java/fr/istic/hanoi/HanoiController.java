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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

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
    private MenuItem about;
    
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
    private Integer numberOfDisks;
    private HanoiShape shape;
    private ImageView zinzinView;
    private Scene scene;
    private HanoiAnimation animation;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	Logger.getGlobal().info("Controller initialisation");
    	initializeView();
        this.numberOfDisks = 3;
        initializeGame();        
        grid.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.scene = newScene;
                this.animation = new HanoiAnimation(zinzinView, heightDisk, piliers, scene, discViews, this);
                this.animation.animationZinzinOnFirstPilier();
                
                scene.widthProperty().addListener(event -> {
                    this.animation.animationZinzinOnFirstPilier();
                });
            }
        });
        
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
        dragEvent.acceptTransferModes(TransferMode.MOVE);    
        int colonne = getColonne(dragEvent.getX());
        for(int i = 0; i < 3; i++) {
        	Rectangle pilier = (Rectangle) piliers.get(i).getChildren().get(0);
        	if(i==colonne)
        		pilier.setFill(shape.getLinearGradient());
        	else
        		pilier.setFill(Color.BROWN);
        }
        dragEvent.consume();
    }
    
    private void onDragDone(DragEvent dragEvent) {
        if (dragEvent.getTransferMode() == TransferMode.MOVE) {
            dragEvent.getDragboard().clear();
        }
       if(piliers.get(2).getChildren().size()-1 == this.numberOfDisks) {
        	this.animation.animationZinzinGoToLastPilier();
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
    		this.animation.animation(r,piliers.get(colonneCible), pilier.getHeight());
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
    
    public void initializeGame() {
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
              
        Logger.getGlobal().info(String.format("taille stack : " + model.getStack(1).size()));
    }
    
    private void initializeView() {
    	Image zinzin = new Image(System.getProperty("user.dir") + "\\src\\main\\resources\\fr\\istic\\images\\zinzin.png");
    	this.zinzinView = new ImageView(zinzin);
    	monBorderPane.getChildren().add(zinzinView);
    	zinzinView.setFitWidth(200); 
        zinzinView.setFitHeight(150);
        zinzinView.setTranslateY(20);
                
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
        about.setOnAction(this::handleMenuItemAboutClick);
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
    
    private void handleMenuItemAboutClick(ActionEvent event) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Hanoi :");
        alert.setHeaderText(null);
        alert.setContentText("-La Tour de Hanoï est un jeu de puzzle avec trois piliers et des disques de différentes tailles empilés. \n" +
        		"-L'objectif est de déplacer tous les disques vers le denier pilier en suivant ces règles : \n\n" +
        		" 1) Vous ne pouvez déplacer qu'un disque à la fois. \n" +
        		" 2) Un disque plus grand ne peut pas être placé sur un plus petit. \n" +
        		"-Le but est de reproduire la pile initiale sur le dernier pilier. \n" + 
        		"En cas de reussite, les Zinzins de l'espace ne manqueront pas de durcir la difficulté");
        alert.showAndWait();
    }

	public Integer getNumberOfDisks() {
		return numberOfDisks;
	}

	public void setNumberOfDisks(Integer numberOfDisks) {
		this.numberOfDisks = numberOfDisks;
	}
    
}