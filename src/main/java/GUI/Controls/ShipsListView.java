package GUI.Controls;

import GUI.Stages.ShipEditStage;
import Model.Ship;
import Model.Database.Database;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;

public class ShipsListView extends ListView<ListViewRow<Ship>> {

    private class ShipRow extends ListViewRow<Ship> {

        ShipRow() { super(); }

        ShipRow(String label, Node... nodes) {
            super(label, nodes);
        }
    }

    private static ShipsListView ourInstance = new ShipsListView();

    private enum State {
        SyncWithDB,
        AddNewRow
    }

    public static ShipsListView getInstance() {
        return ourInstance;
    }

    private ShipsListView() {
        super();
        setPrefSize(200, 200);
        for (Ship ship : Database.getInstance().getShipsTable().selectAll()) {
            addShipRow(ship, State.SyncWithDB);
        }
        getItems().add(new ShipRow("New...", new CustomButton("+", this::createShip)));
    }

    private void createShip() {
        Ship ship = openShipEditStage();
        if (ship == null) {
            return;
        }
        Database.getInstance().getShipsTable().insert(ship);
        ship.setId(Database.getInstance().getShipsTable().getIdCounter());
        addShipRow(ship, State.AddNewRow);
    }

    private void addShipRow(Ship ship, State state) {
        ShipRow shipRow = new ShipRow();
        shipRow.setTarget(ship);
        shipRow.setLabel(ship.toString());
        shipRow.add(new CustomButton("Edit", editShip(shipRow)));
        shipRow.add(new CustomButton("-", removeShip(shipRow)));
        switch (state) {
            case SyncWithDB:
                getItems().add(shipRow);
                break;
            case AddNewRow:
                getItems().add(getItems().size() - 1, shipRow);
                break;
        }
        final Tooltip tooltip = new Tooltip();
        tooltip.setText(
                String.format(
                        "Id: %d, Name: %s, Tonnage: %e, MaxVelocity: %e, FuelAmount: %e, FuelConsumptionRate: %e",
                        ship.getId(), ship.toString(), ship.getTonnage(), ship.getMaxVelocity(), ship.getFuelAmount(), ship.getFuelConsumptionRate()
                )
        );
        shipRow.setTooltip(tooltip);
    }

    private Ship openShipEditStage() {
        ShipEditStage stage = new ShipEditStage();
        return stage.getData();
    }

    private void updateShip(ShipRow shipRow, Ship target) {
        if (target == null) {
            return;
        }
        target.setId(shipRow.getTarget().getId());
        shipRow.setTarget(target);
        shipRow.setLabel(target.toString());
        Database.getInstance().getShipsTable().update(target);
    }

    private ActionDelegate editShip(final ShipRow shipRow) {
        return () -> {
            updateShip(shipRow, openShipEditStage());
        };
    }

    private ActionDelegate removeShip(ShipRow shipRow) {
        return () -> {
            Database.getInstance().getShipsTable().delete(shipRow.getTarget());
            getItems().remove(shipRow);
        };
    }
}
