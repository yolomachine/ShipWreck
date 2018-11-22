package GUI.Stages;

import GUI.Controls.ActionDelegate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

class CustomStage extends Stage {

    CustomStage(String rootLoc, ImageView icon, String title, int minWidth, int minHeight, ActionDelegate onCreate) {
        onCreate.invoke();

        Parent root = null;
        try {
            root = FXMLLoader.load(new File(rootLoc).toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getIcons().add(icon.getImage());
        setTitle(title);
        setScene(new Scene(root));
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }


}
