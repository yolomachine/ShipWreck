package GUI.Controls;

import GUI.Stages.ShipEditStage;
import Model.Ship;
import Model.ShipDB;

class ShipHandler {

    private static ShipHandler ourInstance = new ShipHandler();

    static ShipHandler getInstance() {
        return ourInstance;
    }

    private ShipHandler() { }

    Ship createShip() {
        Ship ship = openShipEditStage();
        if (ship == null) {
            return null;
        }
        ShipDB.getInstance().insertShip(ship);
        ship.setId(ShipDB.getInstance().getShipIdCounter());
        return ship;
    }

    Ship editShip(Ship ship) {
        if (ship == null) {
            return null;
        }
        Ship edited = openShipEditStage();
        edited.setId(ship.getId());
        ShipDB.getInstance().updateShip(edited);
        return edited;
    }

    void deleteShip(Ship ship) {
        if (ship == null) {
            return;
        }
        ShipDB.getInstance().deleteShip(ship);
    }

    private Ship openShipEditStage() {
        ShipEditStage stage = new ShipEditStage();
        return stage.getData();
    }

}
