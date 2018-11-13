package Model.Database;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.mysql.MysObjects;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Table {
    protected DbTable table;
    protected DbColumn idColumn;
    protected int idCounter;

    public Table(String name) {
        this.table = Database.getInstance().getDbSchema().addTable(name);
        addColumnsDefinitions();
        String createTableQuery =
                new CreateTableQuery(table, true)
                        .addCustomization(MysObjects.IF_NOT_EXISTS_TABLE)
                        .validate()
                        .toString();
        idCounter = getMaxId();
        try {
            Database
                    .getInstance()
                    .getConnection()
                    .createStatement()
                    .execute(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected abstract void addColumnsDefinitions();

    public int getIdCounter() {
        return idCounter;
    }

    protected String buildSelectQuery() {
        return new SelectQuery()
                .addAllTableColumns(table)
                .addOrdering(idColumn, OrderObject.Dir.ASCENDING)
                .validate()
                .toString();
    };

    protected String buildSelectQuery(BinaryCondition condition) {
        return new SelectQuery()
                .addAllTableColumns(table)
                .addCondition(condition)
                .addOrdering(idColumn, OrderObject.Dir.ASCENDING)
                .validate()
                .toString();
    };

    protected String buildInsertQuery(ArrayList<Pair<DbColumn, Object>> insertPairs) {
        InsertQuery insertQuery = new InsertQuery(table);
        for (Pair<DbColumn, Object> pair : insertPairs) {
            insertQuery.addColumn(pair.getKey(), pair.getValue());
        }
        return insertQuery.validate().toString();
    };

    protected String buildDeleteQuery(BinaryCondition condition) {
        return new DeleteQuery(table)
                .addCondition(condition)
                .validate()
                .toString();
    };

    protected String buildUpdateQuery(ArrayList<Pair<DbColumn, Object>> columnValuePairs, BinaryCondition condition) {
        UpdateQuery updateQuery = new UpdateQuery(table);
        for (Pair<DbColumn, Object> pair : columnValuePairs) {
            updateQuery.addSetClause(pair.getKey(), pair.getValue());
        }
        return updateQuery
                .addCondition(condition)
                .validate()
                .toString();
    };

    protected int getMaxId() {
        int id = 0;
        String selectQuery =
                new SelectQuery()
                        .addColumns(idColumn)
                        .addCondition(
                                BinaryCondition.equalTo(
                                    idColumn,
                                    new Subquery(
                                            new SelectQuery()
                                                    .addCustomColumns(FunctionCall.max().addColumnParams(idColumn))
                                                    .addFromTable(table)
                                                    .validate()
                                    )
                                )
                        )
                        .validate()
                        .toString();
        try {
            id = Database
                    .getInstance()
                    .getConnection()
                    .createStatement()
                    .executeQuery(selectQuery)
                    .getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}
