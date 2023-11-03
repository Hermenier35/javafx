module fr.istic.hanoi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;
    requires java.logging;
	requires javafx.graphics;


    opens fr.istic.hanoi to javafx.fxml, javafx.graphics, org.junit.platform.commons;
    opens fr.istic.hanoi.test to org.junit.platform.commons;

    exports fr.istic.hanoi;
}
