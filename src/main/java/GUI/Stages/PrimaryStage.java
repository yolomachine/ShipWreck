package GUI.Stages;

import Model.Database.Database;
import Utils.Icons.Icons;

public class PrimaryStage extends CustomStage {

    public PrimaryStage() {
        super(
                "src/main/java/GUI/MainView.fxml",
                Icons.getInstance().getShipIcon(),
                "ShipWreck",
                1000,
                664,
                () -> Database.getInstance().establishConnection()
        );
        setOnCloseRequest(event -> {
            Database.getInstance().closeConnection();
            System.exit(0);
        });
        show();
    }

}
