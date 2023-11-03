package fr.istic.hanoi;

import javafx.scene.input.DataFormat;

public class MyCustomDataFormat extends DataFormat {
	 public static final DataFormat RECTANGLE = new MyCustomDataFormat("application/x-rectangle");

	    private MyCustomDataFormat(String mimeType) {
	        super(mimeType);
	    }
}
