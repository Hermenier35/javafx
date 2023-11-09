package fr.istic.hanoi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class HanoiAnimation {
	
	private ImageView zinzinView;
	private int heightDisk;
	private ArrayList<StackPane> piliers = new ArrayList<>();
	private Scene scene;
	private Map<Integer, MyRectangle> discViews = new HashMap<>();
	private HanoiController controller;
	
	
	
	public HanoiAnimation(ImageView zinzinView, int heightDisk, ArrayList<StackPane> piliers, Scene scene,
			Map<Integer, MyRectangle> discViews, HanoiController controller) {
		this.zinzinView = zinzinView;
		this.heightDisk = heightDisk;
		this.piliers = piliers;
		this.scene = scene;
		this.discViews = discViews;
		this.controller = controller;
	}

	public void animation(MyRectangle disk, StackPane pilierTatget, double hauteurPilier) {
    	int hauteur = pilierTatget.getChildren().size()-2;
    	TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), disk);
        translateTransition.setByY(hauteurPilier - (hauteur * heightDisk));
        translateTransition.setCycleCount(1);
        translateTransition.play();
    }
    
    public void animationZinzinOnFirstPilier() {
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
    }
    
    public void animationZinzinGoToLastPilier() {
    	Rectangle lastPilier = (Rectangle)piliers.get(2).getChildren().get(0);
    	TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), this.zinzinView);
    	translateTransition.setToX(lastPilier.getParent().getLayoutX());
    	translateTransition.setCycleCount(1);
    	translateTransition.setOnFinished(event -> {
    		ObservableList<Node> children = piliers.get(2).getChildren();
            for(int i = 1; i < children.size(); i++) {
                animationRecupDisk((MyRectangle)children.get(i));
            }
    	});
    	translateTransition.play();
    }
    
    private void animationRecupDisk(MyRectangle disk) {
    	TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), disk);
    	translateTransition.setToY(-(this.scene.getHeight()-(this.zinzinView.getY() + this.zinzinView.getFitHeight()+ this.heightDisk)));
    	Interpolator customInterpolator = new Interpolator() {
            @Override
            protected double curve(double t) {
                return t * t * t * t;
            }
        };

        translateTransition.setInterpolator(customInterpolator);
        translateTransition.setCycleCount(1);
        translateTransition.setOnFinished(event -> {
        	translateTransition.getNode().setVisible(false);
    		disk.setColonne(0);
        	if(disk.equals(piliers.get(2).getChildren().get(1))) {
                animationZinzinGoToFirstPilier();
        	}
        });
        translateTransition.play();
    }
    
    private void animationZinzinGoToFirstPilier() {
    	Rectangle firstPilier = (Rectangle)piliers.get(0).getChildren().get(0);
    	TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), this.zinzinView);
    	translateTransition.setToX(firstPilier.getParent().getLayoutX());
    	translateTransition.setCycleCount(1);
    	translateTransition.setOnFinished(event -> {
        	animationDeposeDisk();
    	});
    	translateTransition.play();
    }
    
    private void animationDeposeDisk() {
    	ObservableList<Node> children =  this.piliers.get(2).getChildren();
    	int nbrChildren = children.size();
    	for(int i = 1; i < nbrChildren; i++) {
    		MyRectangle disk = this.discViews.get(i);
	        disk.setVisible(true);
    		piliers.get(2).getChildren().remove(disk);
    		piliers.get(0).getChildren().add(disk);
	    	TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(i), disk);
	    	translateTransition.setByY((this.scene.getHeight()-(this.zinzinView.getY() + this.zinzinView.getFitHeight()+ i* this.heightDisk)));
	    	Interpolator customInterpolator = new Interpolator() {
	            @Override
	            protected double curve(double t) {
	                return t * t * t * t;
	            }
	        };
	        translateTransition.setInterpolator(customInterpolator);
	        translateTransition.setCycleCount(1);
	        translateTransition.setOnFinished(event -> {
	        	if(disk.getNumberDisk()== nbrChildren-1) {
            		this.controller.setNumberOfDisks(controller.getNumberOfDisks()+1);
            		controller.initializeGame();
            	}
	        });
	        translateTransition.play();
    	}
    }
}
