package GUI.Stages;

import Model.Ship;
import Utils.Icons.Icons;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class ShipEditStage extends CustomModalStage<Ship> {
    TextField nameTextField;
    Spinner<Double> tonnageSpinner;
    Spinner<Double> maxVelocitySpinner;
    Spinner<Double> fuelAmountSpinner;
    Spinner<Double> fuelConsumptionRateSpinner;

    public ShipEditStage() {
        super(
                "src/main/java/GUI/ShipEditView.fxml",
                Icons.getInstance().getShipIcon(),
                "Edit",
                300,
                300,
                () -> {}
        );
        Scene scene = getScene();
        nameTextField = (TextField) scene.lookup("#nameTextField");
        tonnageSpinner = (Spinner<Double>) scene.lookup("#tonnageSpinner");
        maxVelocitySpinner = (Spinner<Double>) scene.lookup("#maxVelocitySpinner");
        fuelAmountSpinner = (Spinner<Double>) scene.lookup("#fuelAmountSpinner");
        fuelConsumptionRateSpinner = (Spinner<Double>) scene.lookup("#fuelConsumptionRateSpinner");
        setOnConfirm(() -> {
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
    }

    public ShipEditStage(Ship ship) {
        this();
        if (ship == null) {
            return;
        }
        nameTextField.setText(ship.toString());
        tonnageSpinner.getValueFactory().setValue(ship.getTonnage());
        maxVelocitySpinner.getValueFactory().setValue(ship.getMaxVelocity());
        fuelAmountSpinner.getValueFactory().setValue(ship.getFuelAmount());
        fuelConsumptionRateSpinner.getValueFactory().setValue(ship.getFuelConsumptionRate());
    }
}
