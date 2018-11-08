package Model;

import org.geotools.map.Layer;

import java.sql.*;
import java.util.ArrayList;

public class ShipWreckDB {

    private static int shipIdCounter = 0;
    private static int routeIdCounter = 0;
    private static Connection connection = null;
    private static ShipWreckDB ourInstance = new ShipWreckDB();

    public static ShipWreckDB getInstance() {
        return ourInstance;
    }

    public int getShipIdCounter() {
        return shipIdCounter;
    }

    public int getRouteIdCounter() {
        return routeIdCounter;
    }

    private ShipWreckDB() {
        try {
            String driverName = "org.sqlite.JDBC";
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't get class. No driver found");
            e.printStackTrace();
        }

        establishConnection();

        try {
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS Ships (\n"
                    + "    ShipId              INTEGER       PRIMARY KEY ASC NOT NULL,\n"
                    + "    Name                VARCHAR (255),\n"
                    + "    Tonnage             DOUBLE,\n"
                    + "    MaxVelocity         DOUBLE,\n"
                    + "    FuelAmount          DOUBLE,\n"
                    + "    FuelConsumptionRate DOUBLE\n"
                    + ");"
            );
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS Routes (\n"
                    + "    RouteId INTEGER PRIMARY KEY ASC NOT NULL,\n"
                    + "    ShipId  INTEGER REFERENCES Ships (ShipId),\n"
                    + "    Name    VARCHAR (255),\n"
                    + "    Points  BLOB\n"
                    + ");\n"
            );
            shipIdCounter = getMaxId("Ships", "ShipId");
            routeIdCounter = getMaxId("Routes", "RouteId");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }

    private int getMaxId(String tableName, String columnName) {
        int id = 0;
        try {
            String query = String.format(
                    "SELECT %s FROM %s WHERE %s=(SELECT MAX(%s) from %s)",
                    columnName, tableName, columnName, columnName, tableName
            );
            id = connection.createStatement().executeQuery(query).getInt(columnName);
        } catch (SQLException ignored) { }
        return id;
    }

    public ArrayList<Ship> getShips() {
        ArrayList<Ship> ships = new ArrayList<>();
        try {
            String query = "SELECT * FROM Ships ORDER BY ShipId ASC";
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                ships.add(
                        new Ship(
                                result.getInt("ShipId"),
                                result.getString("Name"),
                                result.getDouble("Tonnage"),
                                result.getDouble("MaxVelocity"),
                                result.getDouble("FuelAmount"),
                                result.getDouble("FuelConsumptionRate")
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ships;
    }

    public ArrayList<Route> getRoutes(int shipId) {
        ArrayList<Route> routes = new ArrayList<>();
        try {
            String query = "SELECT * FROM Routes WHERE ShipId = ? ORDER BY RouteId ASC";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, shipId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                routes.add(
                        new Route(
                                result.getInt("RouteId"),
                                result.getInt("ShipId"),
                                result.getString("Name"),
                                result.getBytes("Points")
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public void insertShip(Ship ship) {
        try {
            String query =
                    "INSERT INTO Ships (\n"
                    + "    ShipId,\n"
                    + "    Name,\n"
                    + "    Tonnage,\n"
                    + "    MaxVelocity,\n"
                    + "    FuelAmount,\n"
                    + "    FuelConsumptionRate\n"
                    + ")\n"
                    + "VALUES (\n"
                    + "    ?,\n"
                    + "    ?,\n"
                    + "    ?,\n"
                    + "    ?,\n"
                    + "    ?,\n"
                    + "    ?\n"
                    + ")";
            ++shipIdCounter;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, shipIdCounter);
            statement.setString(2, ship.toString());
            statement.setDouble(3, ship.getTonnage());
            statement.setDouble(4, ship.getMaxVelocity());
            statement.setDouble(5, ship.getFuelAmount());
            statement.setDouble(6, ship.getFuelConsumptionRate());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteShip(Ship ship) {
        try {
            String query = "DELETE FROM Ships WHERE ShipId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, ship.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateShip(Ship ship) {
        try {
            String query =
                    "UPDATE Ships SET \n"
                    + "Name = ?,\n"
                    + "Tonnage = ?,\n"
                    + "MaxVelocity = ?,\n"
                    + "FuelAmount = ?,\n"
                    + "FuelConsumptionRate = ?\n"
                    + "WHERE ShipId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, ship.toString());
            statement.setDouble(2, ship.getTonnage());
            statement.setDouble(3, ship.getMaxVelocity());
            statement.setDouble(4, ship.getFuelAmount());
            statement.setDouble(5, ship.getFuelConsumptionRate());
            statement.setDouble(6, ship.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRoute(Route route) {
        try {
            Layer layer = route.calculate();
            if (layer == null) {
                return;
            }
            Map.getInstance().addLayer(layer);
            String query =
                    "INSERT INTO Routes (\n"
                            + "    RouteId,\n"
                            + "    ShipId,\n"
                            + "    Name,\n"
                            + "    Points\n"
                            + ")\n"
                            + "VALUES (\n"
                            + "    ?,\n"
                            + "    ?,\n"
                            + "    ?,\n"
                            + "    ?\n"
                            + ")";
            ++routeIdCounter;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, routeIdCounter);
            statement.setInt(2, route.getShipId());
            statement.setString(3, route.toString());
            statement.setBytes(4, route.toBlob());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRoute(Route route) {
        try {
            String query = "DELETE FROM Routes WHERE RouteId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, route.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRoute(Route route) {
        try {
            String query = "UPDATE Routes SET Points = ? WHERE RouteId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBytes(1, route.toBlob());
            statement.setDouble(2, route.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void establishConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:shipwreck.db");
        } catch (SQLException e) {
            System.out.println("Can't get connection. Incorrect URL");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Can't close connection");
            e.printStackTrace();
        }
    }
}
