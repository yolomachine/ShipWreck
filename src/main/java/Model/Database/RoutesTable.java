package Model.Database;

import Model.Route;
import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import javafx.util.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoutesTable extends Table {
    private DbColumn shipId;
    private DbColumn name;
    private DbColumn points;

    public RoutesTable() {
        super("Routes");
    }

    @Override
    protected void addColumnsDefinitions() {
        idColumn = table.addColumn("RouteId", "integer", null);
        shipId = table.addColumn("ShipId", "integer", null);
        name = table.addColumn("Name", "varchar", 255);
        points = table.addColumn("Points", "blob", null);

        idColumn.primaryKey();
        shipId.references("ShipId_fk", "Ships", "ShipId");
    }

    private ArrayList<Route> parseResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Route> nodes = new ArrayList<>();
        while (resultSet.next()) {
            nodes.add(
                    new Route(
                            resultSet.getInt("RouteId"),
                            resultSet.getInt("ShipId"),
                            resultSet.getString("Name"),
                            resultSet.getBytes("Points")
                    )
            );
        }
        return nodes;
    }

    private ArrayList<Pair<DbColumn, Object>> makeColumnValuePairs(Route route) {
        ArrayList<Pair<DbColumn, Object>> insertPairs = new ArrayList<>();
        insertPairs.add(new Pair<>(idColumn, route.getId()));
        insertPairs.add(new Pair<>(shipId, route.getShipId()));
        insertPairs.add(new Pair<>(name, route.toString()));
        insertPairs.add(new Pair<>(points, "?"));
        return insertPairs;
    }

    public ArrayList<Route> selectAll() {
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

    public ArrayList<Route> selectWhereId(int id) {
        try {
            return parseResultSet(
                    Database
                            .getInstance()
                            .getConnection()
                            .createStatement()
                            .executeQuery(buildSelectQuery(BinaryCondition.equalTo(shipId, id)))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Route route){
        route.calculate();
        route.setId(++idCounter);
        try {
            PreparedStatement statement =
                    Database
                            .getInstance()
                            .getConnection()
                            .prepareStatement(
                                    buildInsertQuery(makeColumnValuePairs(route)).replace("'?'", "?")
                            );
            statement.setBytes(1, route.toBlob());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Route route) {
        try {
            Database
                    .getInstance()
                    .getConnection()
                    .createStatement()
                    .executeUpdate(buildDeleteQuery(BinaryCondition.equalTo(idColumn, route.getId())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Route route) {
        try {
            PreparedStatement statement =
                    Database
                            .getInstance()
                            .getConnection()
                            .prepareStatement(buildUpdateQuery(
                                    makeColumnValuePairs(route),
                                    BinaryCondition.equalTo(idColumn, route.getId())
                            ));
            statement.setBytes(1, route.toBlob());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}