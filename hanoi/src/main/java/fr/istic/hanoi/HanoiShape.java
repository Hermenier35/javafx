package fr.istic.hanoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class HanoiShape{
	
	private int nbrDisk;
	private double windowsWidth;
	private List<MyRectangle> rectangles;
	private int heightDisk;
	
	

	public HanoiShape(int nbrDisk, double windowsWidth, int heightDisk) {
		this.nbrDisk = nbrDisk;
		this.windowsWidth = windowsWidth;
		this.rectangles = new ArrayList<>();
		this.heightDisk = heightDisk;
		this.buildDisk();
	}



	public  Rectangle getCylinder() {
		int heightCylinder = nbrDisk * heightDisk + 20;
		Rectangle rect = new Rectangle(15, heightCylinder, Color.BROWN);
		rect.setArcHeight(20);
		rect.setArcWidth(20);
		return rect; 
	}
	
	private void buildDisk() {
		int spaceBetweenDisks = 10;
		int widestDisk = (int)this.windowsWidth/3 - (spaceBetweenDisks/2);
		ArrayList<Color> color = new ArrayList<>();
		color.add(Color.DEEPPINK);
		color.add(Color.ANTIQUEWHITE);
		color.add(Color.AQUA);
		color.add(Color.AQUAMARINE);
		color.add(Color.DARKTURQUOISE);
		color.add(Color.BLACK);
		color.add(Color.BEIGE);
		color.add(Color.BLANCHEDALMOND);
		color.add(Color.BROWN);
		
		for(int i = 0; i< this.nbrDisk; i++) {
			rectangles.add(new MyRectangle(widestDisk, heightDisk, color.get(i), 0));
			widestDisk-=30;
		}
		
		//Collections.reverse(rectangles);
	}



	public List<MyRectangle> getRectangles() {
		return rectangles;
	}
	
	
}
  