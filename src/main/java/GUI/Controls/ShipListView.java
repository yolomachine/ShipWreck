package GUI.Controls;

import GUI.Stages.ShipEditStage;
import Model.Ship;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ShipListView extends ListView<ListViewRow<Ship>> {

    private class ShipRow extends ListViewRow<Ship> {

        ShipRow() { super(); }

        ShipRow(String label) { super(label); }

        ShipRow(String label, Node... nodes) {
            super(label, nodes);
        }
    }

    private static class ColorRectCell extends ListCell<ShipRow> {
        @Override
        public void updateItem(ShipRow item, boolean empty) {
            super.updateItem(item, empty);
            setHeight(item.getHeight());
        }
    }

    public ShipListView() {
        super();
        setPrefSize(200, 200);
        ShipRow shipRow = new ShipRow("New...", new CustomButton("+", this::createShip));
        shipRow.setLabelAlignment(Pos.CENTER_RIGHT);
        getItems().add(shipRow);
    }


    private void createShip() {
        ShipRow shipRow = new ShipRow();
        edit(shipRow).invoke();
        if (shipRow.getTarget() == null) {
            return;
        }
        shipRow.add(new CustomButton("Edit", edit(shipRow)));
        shipRow.add(new CustomButton("-", remove(shipRow)));
        if (getItems().size() == 0) {
            getItems().add(shipRow);
        } else {
            getItems().add(getItems().size() - 1, shipRow);
        }
    }

    private ActionDelegate edit(final ShipRow shipRow) {
        return () -> {
            ShipEditStage ses = new ShipEditStage();
            shipRow.setTarget(ses.getData());
            shipRow.setLabel(ses.getData().name);
        };
    }

    private ActionDelegate remove(ShipRow shipRow) {
        return () -> {
            getItems().remove(shipRow);
        };
    }
}
