package GUI.Stages;

import Model.Geo.Point;
import Model.Route;
import Utils.Icons.Icons;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class RouteEditStage extends CustomModalStage<Route> {
    private TextField nameTextField;
    private Spinner<Double> startLatitude;
    private Spinner<Double> startLongitude;
    private Spinner<Double> destinationLatitude;
    private Spinner<Double> destinationLongitude;

    public RouteEditStage() {
        super(
                "src/main/java/GUI/RouteEditView.fxml",
                Icons.getInstance().getShipIcon(),
                "Edit",
                300,
                300,
                () -> {}
        );
        Scene scene = getScene();
        nameTextField = (TextField) scene.lookup("#nameTextField");
        startLatitude = (Spinner<Double>) scene.lookup("#startLatitudeSpinner");
        startLongitude = (Spinner<Double>) scene.lookup("#startLongitudeSpinner");
        destinationLatitude = (Spinner<Double>) scene.lookup("#destinationLatitudeSpinner");
        destinationLongitude = (Spinner<Double>) scene.lookup("#destinationLongitudeSpinner");
        setOnConfirm(() -> {
            target = new Route(
                    nameTextField.getText(),
                    new Point(startLatitude.getValue(), startLongitude.getValue()),
                    new Point(destinationLatitude.getValue(), destinationLongitude.getValue())
            );
            close();
        });
        setOnCancel(this::close);
    }

    public RouteEditStage(Route route) {
        this();
        if (route == null) {
            return;
        }
        nameTextField.setText(route.toString());
        startLatitude.getValueFactory().setValue(route.getPoints().get(0).getLat());
        startLongitude.getValueFactory().setValue(route.getPoints().get(0).getLon());
        destinationLatitude.getValueFactory().setValue(route.getPoints().get(route.getPoints().size() - 1).getLat());
        destinationLongitude.getValueFactory().setValue(route.getPoints().get(route.getPoints().size() - 1).getLon());
    }
}
