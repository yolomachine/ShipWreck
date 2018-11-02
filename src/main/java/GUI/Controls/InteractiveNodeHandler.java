package GUI.Controls;

import GUI.Stages.RouteEditStage;
import GUI.Stages.ShipEditStage;
import Model.Route;
import Model.Ship;
import Model.ShipWreckDB;

public class InteractiveNodeHandler {

    private static InteractiveNodeHandler ourInstance = new InteractiveNodeHandler();

    static InteractiveNodeHandler getInstance() {
        return ourInstance;
    }

    private InteractiveNodeHandler() { }

    InteractiveNode create(InteractiveNode parent) {
        InteractiveNode node = null;
        switch (parent.getType()) {
            case Root:
                Ship ship = openShipEditStage();
                if (ship == null) {
                    return null;
                }
                ShipWreckDB.getInstance().insertShip(ship);
                ship.setId(ShipWreckDB.getInstance().getShipIdCounter());
                node = ship;
                break;
            case Ship:
                Route route = openRouteEditStage();
                if (route == null) {
                    return null;
                }
                route.setShipId(((Ship) parent).getId());
                ShipWreckDB.getInstance().insertRoute(route);
                route.setId(ShipWreckDB.getInstance().getRouteIdCounter());
                node = route;
                break;
        }
        return node;
    }

    InteractiveNode edit(InteractiveNode node) {
        if (node == null) {
            return null;
        }
        InteractiveNode edited = null;
        switch (node.getType()) {
            case Root:
                return null;
            case Ship:
                Ship ship = openShipEditStage();
                if (ship == null) {
                    return null;
                }
                ship.setId(((Ship) node).getId());
                ShipWreckDB.getInstance().updateShip(ship);
                edited = ship;
                break;
            case Route:
                Route route = openRouteEditStage();
                if (route == null) {
                    return null;
                }
                route.setId(((Route) node).getId());
                ShipWreckDB.getInstance().updateRoute(route);
                edited = route;
                break;
        }
        return edited;
    }

    void delete(InteractiveNode node) {
        if (node == null) {
            return;
        }
        switch (node.getType()) {
            case Root:
                return;
            case Ship:
                ShipWreckDB.getInstance().deleteShip((Ship) node);
                break;
            case Route:
                ShipWreckDB.getInstance().deleteRoute((Route) node);
                break;
        }
    }

    private Ship openShipEditStage() {
        ShipEditStage stage = new ShipEditStage();
        return stage.getData();
    }

    private Route openRouteEditStage() {
        RouteEditStage stage = new RouteEditStage();
        return stage.getData();
    }

}
