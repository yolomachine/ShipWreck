package Model.Database;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;

import java.sql.*;

public class Database {

    private static Database ourInstance = new Database();
    private static Connection connection;
    private static DbSpec dbSpec = new DbSpec();
    private static DbSchema dbSchema = dbSpec.addDefaultSchema();

    private static ShipsTable shipsTable;
    private static RoutesTable routesTable;

    private Database() {
        try {
            String driverName = "org.sqlite.JDBC";
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't get class. No driver found");
            e.printStackTrace();
        }

        establishConnection();
        closeConnection();
    }

    public static Database getInstance() {
        return ourInstance;
    }

    public Connection getConnection() {
        return connection;
    }

    public DbSpec getDbSpec() {
        return dbSpec;
    }

    public DbSchema getDbSchema() {
        return dbSchema;
    }

    public ShipsTable getShipsTable() {
        if (shipsTable == null) {
            shipsTable = new ShipsTable();
        }
        return shipsTable;
    }

    public RoutesTable getRoutesTable() {
        if (routesTable == null) {
            routesTable = new RoutesTable();
        }
        return routesTable;
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
