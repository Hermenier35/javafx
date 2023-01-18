module fr.istic.hanoi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;
    requires java.logging;


    opens fr.istic.hanoi to javafx.fxml, org.junit.platform.commons;
    opens fr.istic.hanoi.test to org.junit.platform.commons;

    exports fr.istic.hanoi;
}