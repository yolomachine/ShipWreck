package Model.Database;

import Model.Ship;
import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShipsTable extends Table {
    private DbColumn name;
    private DbColumn tonnage;
    private DbColumn maxVelocity;
    private DbColumn fuelAmount;
    private DbColumn fuelConsumptionRate;

    public ShipsTable() {
        super("Ships");
    }

    @Override
    protected void addColumnsDefinitions() {
        idColumn = table.addColumn("ShipId", "integer", null);
        name = table.addColumn("Name", "varchar", 255);
        tonnage = table.addColumn("Tonnage", "double", null);
        maxVelocity = table.addColumn("MaxVelocity", "double", null);
        fuelAmount = table.addColumn("FuelAmount", "double", null);
        fuelConsumptionRate = table.addColumn("FuelConsumptionRate", "double", null);

        idColumn.primaryKey();
    }

    private ArrayList<Ship> parseResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Ship> nodes = new ArrayList<>();
        while (resultSet.next()) {
            nodes.add(
                    new Ship(
                            resultSet.getInt("ShipId"),
                            resultSet.getString("Name"),
                            resultSet.getDouble("Tonnage"),
                            resultSet.getDouble("MaxVelocity"),
                            resultSet.getDouble("FuelAmount"),
                            resultSet.getDouble("FuelConsumptionRate")
                    )
            );
        }
        return nodes;
    }

    private ArrayList<Pair<DbColumn, Object>> makeColumnValuePairs(Ship ship) {
        ArrayList<Pair<DbColumn, Object>> insertPairs = new ArrayList<>();
        insertPairs.add(new Pair<>(idColumn, ship.getId()));
        insertPairs.add(new Pair<>(name, ship.toString()));
        insertPairs.add(new Pair<>(tonnage, ship.getTonnage()));
        insertPairs.add(new Pair<>(maxVelocity, ship.getMaxVelocity()));
        insertPairs.add(new Pair<>(fuelAmount, ship.getFuelAmount()));
        insertPairs.add(new Pair<>(fuelConsumptionRate, ship.getFuelConsumptionRate()));
        return insertPairs;
    }

    public ArrayList<Ship> selectAll() {
        try {
            return parseResultSet(
                    Database
                            .getInstance()
                            .getConnection()
                            .createStatement()
                            .executeQuery(buildSelectQuery())
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Ship> selectWhereId(int id) {
        try {
            return parseResultSet(
                    Database
                            .getInstance()
                            .getConnection()
                            .createStatement()
                            .executeQuery(buildSelectQuery(BinaryCondition.equalTo(idColumn, id)))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Ship ship) {
        ship.setId(++idCounter);
        try {
            Database
                    .getInstance()
                    .getConnection()
                    .createStatement()
                    .executeUpdate(buildInsertQuery(makeColumnValuePairs(ship)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Ship ship) {
        try {
            Database
                    .getInstance()
                    .getConnection()
                    .createStatement()
                    .executeUpdate(buildDeleteQuery(BinaryCondition.equalTo(idColumn, ship.getId())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Ship ship) {
        try {
            Database
                    .getInstance()
                    .getConnection()
                    .createStatement()
                    .executeUpdate(
                            buildUpdateQuery(
                                    makeColumnValuePairs(ship),
                                    BinaryCondition.equalTo(idColumn, ship.getId())
                            )
                    );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
