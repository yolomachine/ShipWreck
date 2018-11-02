package GUI.Stages;

import Model.ShipWreckDB;
import Utils.Icons.Icons;

public class PrimaryStage extends CustomStage {

    public PrimaryStage() {
        super(
                "src/main/java/GUI/MainView.fxml",
                Icons.getInstance().getShipIcon(),
                "ShipWreck",
                1000,
                664,
                () -> ShipWreckDB.getInstance().establishConnection()
        );
        setOnCloseRequest(event -> {
            ShipWreckDB.getInstance().closeConnection();
            System.exit(0);
        });
        show();
    }

}
