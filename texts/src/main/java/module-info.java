module fr.istic.we {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens fr.istic.we to javafx.fxml;
    exports fr.istic.we;
}