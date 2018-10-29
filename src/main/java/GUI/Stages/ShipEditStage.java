package GUI.Stages;

import Model.Ship;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ShipEditStage extends Stage {

    private Ship ship;

    public ShipEditStage() {
        initModality(Modality.APPLICATION_MODAL);
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

        Scene scene = getScene();
        Button confirmButton = (Button) scene.lookup("#confirmButton");
        Button cancelButton = (Button) scene.lookup("#cancelButton");
        TextField nameTextField = (TextField) scene.lookup("#nameTextField");
        Spinner<Double> tonnageSpinner = (Spinner<Double>) scene.lookup("#tonnageSpinner");
        Spinner<Double> maxVelocitySpinner = (Spinner<Double>) scene.lookup("#maxVelocitySpinner");
        Spinner<Double> fuelAmountSpinner = (Spinner<Double>) scene.lookup("#fuelAmountSpinner");
        Spinner<Double> fuelConsumptionRateSpinner = (Spinner<Double>) scene.lookup("#fuelConsumptionRateSpinner");

        confirmButton.setOnAction(event -> {
            ship = new Ship(
                    nameTextField.getText(),
                    tonnageSpinner.getValue(),
                    maxVelocitySpinner.getValue(),
                    fuelAmountSpinner.getValue(),
                    fuelConsumptionRateSpinner.getValue()
            );
            close();
        });
        cancelButton.setOnAction(event -> {
            close();
        });
        showAndWait();
    }

    public Ship getData() {
        return ship;
    }
}
