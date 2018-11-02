package GUI.Stages;

import Model.ShipDB;

public class PrimaryStage extends CustomStage {

    public PrimaryStage() {
        super(
                "src/main/java/GUI/MainView.fxml",
                "file:res/icon.png",
                "ShipWreck",
                1000,
                664,
                () -> ShipDB.getInstance().establishConnection()
        );
        setOnCloseRequest(event -> {
            ShipDB.getInstance().closeConnection();
            System.exit(0);
        });
        show();
    }

}
