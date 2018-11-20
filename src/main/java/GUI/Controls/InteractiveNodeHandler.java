package GUI.Controls;

import GUI.Stages.RouteEditStage;
import GUI.Stages.ShipEditStage;
import Database.Database;

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
                Ship ship = openShipEditStage(null);
                if (ship == null) {
                    return null;
                }
                Database.getInstance().getShipsTable().insert(ship);
                ship.setId(Database.getInstance().getShipsTable().getIdCounter());
                node = ship;
                break;
            case Ship:
                Route route = openRouteEditStage(null);
                if (route == null) {
                    return null;
                }
                route.setShipId(parent.getId());
                route.calculate();
                Database.getInstance().getRoutesTable().insert(route);
                route.setId(Database.getInstance().getRoutesTable().getIdCounter());
                node = route;
                break;
        }
        return node;
    }

    InteractiveNode edit(InteractiveNode parent, InteractiveNode node) {
        if (node == null) {
            return null;
        }
        InteractiveNode edited = null;
        switch (node.getType()) {
            case Root:
                return null;
            case Ship:
                Ship ship = openShipEditStage((Ship) node);
                if (ship == null) {
                    return null;
                }
                ship.setId(node.getId());
                node.invalidate();
                Database.getInstance().getShipsTable().update(ship);
                edited = ship;
                break;
            case Route:
                Route route = openRouteEditStage((Route) node);
                if (route == null) {
                    return null;
                }
                route.setId(node.getId());
                route.setShipId(parent.getId());
                route.setPointsLayer(((Route) node).getPointsLayer());
                route.setLinesLayer(((Route) node).getLinesLayer());
                route.calculate();
                node.invalidate();
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
                node.invalidate();
                Database.getInstance().getShipsTable().delete((Ship) node);
                break;
            case Route:
                node.invalidate();
                Database.getInstance().getRoutesTable().delete((Route) node);
                break;
        }
    }

    private Ship openShipEditStage(Ship ship) {
        ShipEditStage stage = new ShipEditStage(ship);
        stage.showAndWait();
        return stage.getData();
    }

    private Route openRouteEditStage(Route route) {
        RouteEditStage stage = new RouteEditStage(route);
        stage.showAndWait();
        return stage.getData();
    }

}
