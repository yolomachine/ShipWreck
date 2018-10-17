package com.github.yolomachine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(new File("src/main/java/GUI/Scene.fxml").toURI().toURL());

            stage.getIcons().add(new Image("file:res/icon.png"));
            stage.setTitle("ShipWreck");
            stage.setScene(new Scene(root));
            stage.setMinWidth(1000);
            stage.setMinHeight(664);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
