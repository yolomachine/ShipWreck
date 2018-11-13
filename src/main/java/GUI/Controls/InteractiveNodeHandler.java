package GUI.Controls;

import GUI.Stages.RouteEditStage;
import GUI.Stages.ShipEditStage;
import Model.Route;
import Model.Ship;
import Model.Database.Database;

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
                Database.getInstance().getShipsTable().insert(ship);
                ship.setId(Database.getInstance().getShipsTable().getIdCounter());
                node = ship;
                break;
            case Ship:
                Route route = openRouteEditStage();
                if (route == null) {
                    return null;
                }
                route.setShipId(((Ship) parent).getId());
                Database.getInstance().getRoutesTable().insert(route);
                route.setId(Database.getInstance().getRoutesTable().getIdCounter());
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
                Database.getInstance().getShipsTable().update(ship);
                edited = ship;
                break;
            case Route:
                Route route = openRouteEditStage();
                if (route == null) {
                    return null;
                }
                route.setId(((Route) node).getId());
                Database.getInstance().getRoutesTable().update(route);
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
                Database.getInstance().getShipsTable().delete((Ship) node);
                break;
            case Route:
                Database.getInstance().getRoutesTable().delete((Route) node);
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
