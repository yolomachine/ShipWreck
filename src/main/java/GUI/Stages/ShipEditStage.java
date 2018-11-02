package GUI.Stages;

import Model.Ship;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class ShipEditStage extends CustomModalStage<Ship> {

    public ShipEditStage() {
        super(
                "src/main/java/GUI/ShipEditView.fxml",
                "file:res/icon.png",
                "Edit",
                300,
                300,
                () -> {}
        );

        setOnConfirm(() -> {
            Scene scene = getScene();
            TextField nameTextField = (TextField) scene.lookup("#nameTextField");
            Spinner<Double> tonnageSpinner = (Spinner<Double>) scene.lookup("#tonnageSpinner");
            Spinner<Double> maxVelocitySpinner = (Spinner<Double>) scene.lookup("#maxVelocitySpinner");
            Spinner<Double> fuelAmountSpinner = (Spinner<Double>) scene.lookup("#fuelAmountSpinner");
            Spinner<Double> fuelConsumptionRateSpinner = (Spinner<Double>) scene.lookup("#fuelConsumptionRateSpinner");
            target = new Ship(
                    nameTextField.getText(),
                    tonnageSpinner.getValue(),
                    maxVelocitySpinner.getValue(),
                    fuelAmountSpinner.getValue(),
                    fuelConsumptionRateSpinner.getValue()
            );
            close();
        });
        setOnCancel(this::close);
        showAndWait();
    }
}
