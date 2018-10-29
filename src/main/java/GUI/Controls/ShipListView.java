package GUI.Controls;

import GUI.Stages.ShipEditStage;
import Model.Ship;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ShipListView extends ListView {

    private class ShipRow extends ListViewRow<Ship> {
        ShipRow(String label) {
            super(label);
        }

        ShipRow(String label, Node... nodes) {
            super(label, nodes);
        }
    }

    private Stage editView;

    public ShipListView() {
        super();
        setPrefSize(200, 200);
        getItems().add(new ShipRow("New...", new CustomButton("+", this::createShip)));
    }

    private void createShip() {
        addShipRow("Test");
    }

    private void addShipRow(String label, Node... items) {
        ShipRow lvr = new ShipRow(label);
        lvr.add(new CustomButton("Edit", ShipEditStage::new));
        lvr.add(new CustomButton("-", onRemoveListViewRow(lvr)));
        if (getItems().size() == 0) {
            getItems().add(lvr);
        } else {
            getItems().add(getItems().size() - 1, lvr);
        }
    }

    private ActionDelegate onRemoveListViewRow(ShipRow lvr) {
        return () -> {
            getItems().remove(lvr);
        };
    }
}
