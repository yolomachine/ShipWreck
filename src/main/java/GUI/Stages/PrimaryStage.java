package GUI.Stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class PrimaryStage extends Stage {

    public PrimaryStage() {
        Parent root = null;
        try {
            root = FXMLLoader.load(new File("src/main/java/GUI/MainView.fxml").toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        getIcons().add(new Image("file:res/icon.png"));
        setTitle("ShipWreck");
        setScene(new Scene(root));
        setMinWidth(1000);
        setMinHeight(664);
        setOnCloseRequest(event -> System.exit(0));
        show();
    }
}
