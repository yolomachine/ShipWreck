package GUI.Stages;

import Model.Route;

public class RouteEditStage extends CustomModalStage<Route> {


    public RouteEditStage() {
        super(
                "src/main/java/GUI/RouteEditView.fxml",
                "file:res/icon.png",
                "Edit",
                300,
                300,
                () -> {}
        );
        setOnConfirm(() -> {});
        setOnCancel(() -> {});
        showAndWait();
    }

}
