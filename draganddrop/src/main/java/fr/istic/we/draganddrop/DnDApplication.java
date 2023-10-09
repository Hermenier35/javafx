package fr.istic.we.draganddrop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DnDApplication extends Application {

    private static final int STAGE_WIDTH = 1000;
    private static final int STAGE_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DnDApplication.class.getResource("DnD.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), STAGE_WIDTH, STAGE_HEIGHT);
        stage.setTitle("Drag and drop example");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}