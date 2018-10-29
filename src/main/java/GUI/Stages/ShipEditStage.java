package GUI.Stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ShipEditStage extends Stage {

    public ShipEditStage() {
        Parent root = null;
        try {
            root = FXMLLoader.load(new File("src/main/java/GUI/ShipEditView.fxml").toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        getIcons().add(new Image("file:res/icon.png"));
        setTitle("Edit");
        setScene(new Scene(root));
        setMinWidth(300);
        setMinHeight(300);
        setAlwaysOnTop(true);
        show();
    }
}
