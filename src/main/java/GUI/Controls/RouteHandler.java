package GUI.Controls;

import GUI.Stages.RouteEditStage;
import GUI.Stages.ShipEditStage;
import Model.Route;
import Model.Ship;
import Model.ShipDB;

public class RouteHandler {

    private static RouteHandler ourInstance = new RouteHandler();

    static RouteHandler getInstance() {
        return ourInstance;
    }

    private RouteHandler() { }

    Route createRoute() {
        Route route = openRouteEditStage();
        if (route == null) {
            return null;
        }
        ShipDB.getInstance().insertRoute(route);
        route.setId(ShipDB.getInstance().getShipIdCounter());
        return route;
    }

    Route editRoute(Route route) {
        if (route == null) {
            return null;
        }
        Route edited = openRouteEditStage();
        edited.setId(route.getId());
        ShipDB.getInstance().updateRoute(edited);
        return edited;
    }

    void deleteRoute(Route route) {
        if (route == null) {
            return;
        }
        ShipDB.getInstance().deleteRoute(route);
    }

    private Route openRouteEditStage() {
        RouteEditStage stage = new RouteEditStage();
        return stage.getData();
    }

}
