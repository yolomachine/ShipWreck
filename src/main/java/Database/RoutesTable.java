package Database;

import GUI.Controls.Route;
import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import javafx.util.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoutesTable extends Table<Route> {
    private DbColumn shipId;
    private DbColumn name;
    private DbColumn points;
    private DbColumn color;

    public RoutesTable() {
        super("Routes");
    }

    @Override
    protected void addColumnsDefinitions() {
        idColumn = table.addColumn("RouteId", "integer", null);
        shipId = table.addColumn("ShipId", "integer", null);
        name = table.addColumn("Name", "varchar", 255);
        points = table.addColumn("Points", "blob", null);
        color = table.addColumn("Color", "integer", null);

        idColumn.primaryKey();
        shipId.references("ShipId_fk", "Ships", "ShipId");
    }

    @Override
    protected ArrayList<Route> parseResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Route> nodes = new ArrayList<>();
        while (resultSet.next()) {
            Route route = new Route(
                    resultSet.getInt("RouteId"),
                    resultSet.getInt("ShipId"),
                    resultSet.getString("Name"),
                    resultSet.getBytes("Points"),
                    resultSet.getInt("Color"),
                    "Greedy [Left]"
            );
            route.toShapefile();
            nodes.add(route);
        }
        return nodes;
    }

    @Override
    protected ArrayList<Pair<DbColumn, Object>> makeColumnValuePairs(Route route) {
        ArrayList<Pair<DbColumn, Object>> insertPairs = new ArrayList<>();
        insertPairs.add(new Pair<>(idColumn, route.getId()));
        insertPairs.add(new Pair<>(shipId, route.getShipId()));
        insertPairs.add(new Pair<>(name, route.toString()));
        insertPairs.add(new Pair<>(points, "?"));
        insertPairs.add(new Pair<>(color, route.getColor().getRGB()));
        return insertPairs;
    }

    @Override
    public void insert(Route route){
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

    @Override
    public void update(Route route) {
        try {
            PreparedStatement statement =
                    Database
                            .getInstance()
                            .getConnection()
                            .prepareStatement(
                                    buildUpdateQuery(
                                            makeColumnValuePairs(route),
                                            BinaryCondition.equalTo(idColumn, route.getId())
                                    ).replace("'?'", "?")
                            );
            statement.setBytes(1, route.toBlob());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Route> selectWhereShipId(int id) {
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
}
