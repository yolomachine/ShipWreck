package GUI.Stages;

import Model.Geo.Point;
import Model.Route;
import Utils.Icons.Icons;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class RouteEditStage extends CustomModalStage<Route> {


    public RouteEditStage() {
        super(
                "src/main/java/GUI/RouteEditView.fxml",
                Icons.getInstance().getShipIcon(),
                "Edit",
                300,
                300,
                () -> {}
        );
        setOnConfirm(() -> {
            Scene scene = getScene();
            TextField nameTextField = (TextField) scene.lookup("#nameTextField");
            Spinner<Double> startLatitude = (Spinner<Double>) scene.lookup("#startLatitudeSpinner");
            Spinner<Double> startLongitude = (Spinner<Double>) scene.lookup("#startLongitudeSpinner");
            Spinner<Double> destinationLatitude = (Spinner<Double>) scene.lookup("#destinationLatitudeSpinner");
            Spinner<Double> destinationLongitude = (Spinner<Double>) scene.lookup("#destinationLongitudeSpinner");
            target = new Route(
                    nameTextField.getText(),
                    new Point(startLatitude.getValue(), startLongitude.getValue()),
                    new Point(destinationLatitude.getValue(), destinationLongitude.getValue())
            );
            close();
        });
        setOnCancel(this::close);
        showAndWait();
    }

}
