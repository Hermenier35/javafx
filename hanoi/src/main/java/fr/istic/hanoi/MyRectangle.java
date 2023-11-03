package fr.istic.hanoi;

import java.io.Serializable;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MyRectangle extends Rectangle implements Serializable {
	
	private int colonne;
	private int numberDisk;

	public MyRectangle(double width, double height, Color color, int numberDisk) {
		super(width, height, color);
		this.numberDisk = numberDisk;
		this.colonne = 0;
		this.setArcWidth(height);
		this.setArcHeight(height);
	}

	public int getColonne() {
		return colonne;
	}

	public void setColonne(int colonne) {
		this.colonne = colonne;
	}

	public int getNumberDisk() {
		return numberDisk;
	}

	public void setNumberDisk(int numberDisk) {
		this.numberDisk = numberDisk;
	}
	
}
