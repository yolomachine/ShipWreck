package GUI.Stages;

import GUI.Controls.ActionDelegate;
import Model.ShipDB;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

class CustomStage extends Stage {

    CustomStage(String rootLoc, String iconLoc, String title, int minWidth, int minHeight, ActionDelegate onCreate) {
        onCreate.invoke();

        Parent root = null;
        try {
            root = FXMLLoader.load(new File(rootLoc).toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getIcons().add(new Image(iconLoc));
        setTitle(title);
        setScene(new Scene(root));
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }


}
