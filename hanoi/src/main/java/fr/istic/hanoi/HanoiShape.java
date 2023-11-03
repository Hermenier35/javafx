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
	
	

	public HanoiShape(int nbrDisk, double windowsWidth) {
		this.nbrDisk = nbrDisk;
		this.windowsWidth = windowsWidth;
		this.rectangles = new ArrayList<>();
		this.buildDisk();
	}



	public  Rectangle getCylinder() {
		Rectangle rect = new Rectangle(20, 400, Color.BROWN);
		rect.setArcHeight(20);
		rect.setArcWidth(20);
		return rect; 
	}
	
	public void buildDisk() {
		int spaceBetweenDisks = 10;
		int widestDisk = (int)this.windowsWidth/3 - (spaceBetweenDisks/2);
		ArrayList<Color> color = new ArrayList<>();
		color.add(Color.DEEPPINK);
		color.add(Color.ANTIQUEWHITE);
		color.add(Color.AQUA);
		color.add(Color.AQUAMARINE);
		color.add(Color.AZURE);
		color.add(Color.BEIGE);
		color.add(Color.BISQUE);
		color.add(Color.BLACK);
		color.add(Color.BLANCHEDALMOND);
		color.add(Color.BROWN);
		
		for(int i = 0; i< this.nbrDisk; i++) {
			System.out.println(widestDisk);
			rectangles.add(new MyRectangle(widestDisk, 30, color.get(i), 0));
			widestDisk-=30;
		}
		
		Collections.reverse(rectangles);
	}



	public List<MyRectangle> getRectangles() {
		return rectangles;
	}
	
	
}
  