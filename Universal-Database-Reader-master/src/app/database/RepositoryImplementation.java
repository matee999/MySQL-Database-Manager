package app.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.models.DBNode;
import app.resource.Row;
import app.resource.AttributeType;
import app.resource.ConstraintType;
import app.models.Attribute;
import app.models.AttributeConstraint;
import app.models.entity.Entity;
import app.models.root.RootNode;

public class RepositoryImplementation implements Repository {

    private DBConfig dbConfig;
    private Connection connection;

    public RepositoryImplementation(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    private void initConnection() throws SQLException, ClassNotFoundException {
        String ip = (String) dbConfig.getParameter("mssql_ip");
        String database = (String) dbConfig.getParameter("mssql_database");
        String username = (String) dbConfig.getParameter("mssql_username");
        String password = (String) dbConfig.getParameter("mssql_password");
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:jtds:sqlserver://" + ip + "/" + database, username, password);
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

    @Override
    public RootNode getSchema() {
        try {
            initConnection();

            DatabaseMetaData metaData = connection.getMetaData();
            RootNode root = new RootNode("Root");

            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});

            while (tables.next()) {

                String tableName = tables.getString("TABLE_NAME");
                Entity newTable = new Entity(tableName, root);

                root.addChild(newTable);

                ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, null);

                while (columns.next()) {

                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    String hasDefault = columns.getString("COLUMN_DEF");
                    String isNull = columns.getString("IS_NULLABLE");

                    columnType = columnType.replaceAll(" ", "_");
                    AttributeType type = null;
                    boolean domainValue = false;
                    try {
                        type = AttributeType.valueOf(columnType.toUpperCase());
                    } catch (Exception e) {
                        domainValue = true;
                    }
                    Attribute attribute = new Attribute(columnName, newTable, type);
                    newTable.addChild(attribute);

                    if (hasDefault != null)
                        attribute.addChild(new AttributeConstraint(ConstraintType.DEFAULT_VALUE.name(), attribute, ConstraintType.DEFAULT_VALUE));
                    if (domainValue)
                        attribute.addChild(new AttributeConstraint(ConstraintType.DOMAIN_VALUE.name(), attribute, ConstraintType.DOMAIN_VALUE));
                    if (isNull != null && isNull.equals("NO"))
                        attribute.addChild(new AttributeConstraint(ConstraintType.NOT_NULL.name(), attribute, ConstraintType.NOT_NULL));
                }

                ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName);

                while (primaryKeys.next()) {

                    String columnName = primaryKeys.getString("COLUMN_NAME");

                    Attribute pkAttribute = (Attribute) newTable.getChildByName(columnName);

                    if (pkAttribute != null)
                        pkAttribute.addChild(
                                new AttributeConstraint(ConstraintType.PRIMARY_KEY.name(),
                                        pkAttribute, ConstraintType.PRIMARY_KEY));
                }
            }

            tables = metaData.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});

            while (tables.next()) {

                String tableName = tables.getString("TABLE_NAME");
                Entity fkTable = (Entity) root.getChildByName(tableName);

                ResultSet importedKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);

                while (importedKeys.next()) {
                    Entity pkTable = (Entity) root.getChildByName(importedKeys.getString("PKTABLE_NAME"));

                    fkTable.addRelationFrom(pkTable);
                    pkTable.addRelationTo(fkTable);

                    Attribute fkAttribute = (Attribute) fkTable.getChildByName(importedKeys.getString("FKCOLUMN_NAME"));

                    if (fkAttribute != null) {
                        fkAttribute.addChild(new AttributeConstraint(fkAttribute.getName(), fkAttribute, ConstraintType.FOREIGN_KEY));
                        fkAttribute.setInRelationWith((Attribute) pkTable.getChildByName(importedKeys.getString("PKCOLUMN_NAME")));
                    }
                }
            }

            return root;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    @Override
    public List<Row> get(Entity entity) {
        List<Row> data = new ArrayList<>();

        try {
            initConnection();

            String query = "SELECT * FROM " + entity.getName();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Row row = new Row();
                row.setName(entity.getName());

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                    row.addField(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));

                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return data;
    }

    @Override
    public void insert(Entity entity, Row values) {
        try {
            initConnection();

            StringBuilder query = new StringBuilder("INSERT INTO " + entity.getName());

            query.append(" (");

            int attributeCount = values.getColumns().size();

            for (String attribute : values.getColumns()) {
                query.append(attribute);
                attributeCount -= 1;

                if (attributeCount != 0)
                    query.append(", ");
            }

            query.append(") VALUES (");

            for (int i = 0; i < values.getColumns().size(); i++) {
                query.append("?");
                if (i < values.getColumns().size() - 1)
                    query.append(",");
                else {
                    query.append(")");
                    break;
                }
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());


            attributeCount = 1;
            for (String st : values.getColumns()) {
                preparedStatement.setObject(attributeCount, values.getObject(st));
                attributeCount += 1;
            }

            preparedStatement.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    @Override
    public void delete(Entity entity, HashMap<Attribute, String> values) {
        try {
            initConnection();

            StringBuilder query = new StringBuilder("DELETE FROM " + entity.getName());
            query.append(" WHERE");
            boolean and = false;
            for (Map.Entry<Attribute, String> pair : values.entrySet()) {
                if (and)
                    query.append(" AND");

                query.append(" ");
                query.append(pair.getKey().getName());
                query.append('=');
                if (pair.getKey().getAttributeType() == AttributeType.TEXT ||
                    pair.getKey().getAttributeType() == AttributeType.VARCHAR ||
                    pair.getKey().getAttributeType() == AttributeType.DATE ||
                    pair.getKey().getAttributeType() == AttributeType.DATETIME) {
                        query.append('\'');
                        query.append(pair.getValue());
                        query.append('\'');
                } else
                    query.append(pair.getValue());

                and = true;
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

            preparedStatement.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    @Override
    public List<Row> filterAndSort(Entity origin, List<Attribute> columns, List<Boolean> filters, List<Boolean> sorts, List<Boolean> descendings) {
        List<Row> data = new ArrayList<>();
        try {
            initConnection();

            StringBuilder query = new StringBuilder("SELECT ");

            int values = 0;

            for (Boolean value : filters)
                if (value) values += 1;

            for (int i = 0; i < columns.size(); i++) {
                if (filters.get(i)) {
                    query.append(columns.get(i));
                    values -= 1;

                    if (values != 0)
                        query.append(", ");
                }
            }

            query.append(" FROM ").append(origin.getName());

            values = 0;

            for (Boolean value : sorts)
                if (value) values += 1;

            if (values != 0) {
                query.append(" ORDER BY ");

                for (int i = 0; i < columns.size(); i++) {
                    if (sorts.get(i)) {
                        query.append(columns.get(i));
                        if (descendings.get(i))
                            query.append(" DESC");
                        else
                            query.append(" ASC");

                        values -= 1;

                        if (values != 0)
                            query.append(", ");
                    }
                }
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Row row = new Row();
                row.setName(origin.getName());

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                    row.addField(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));

                data.add(row);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
        return data;
    }

    @Override
    public List<Row> leftJoin(Entity left, Entity right, Row selection) {
        List<Row> data = new ArrayList<>();
        try {
            initConnection();

            StringBuilder query = new StringBuilder("SELECT ");

            for (int i = 0; i < right.getChildCount(); i++) {
                query.append(((DBNode) right.getChildAt(i)).getName());
                if (i != right.getChildCount() - 1) query.append(", ");
            }

            query.append(" FROM ").append(right).append(" WHERE ");

            int relations = 0;

            for (int i = 0; i < right.getChildCount(); i++) {
                Attribute relation = ((Attribute) right.getChildAt(i)).getInRelationWith();

                if (relation != null && relation.getParent().getName().equals(left.getName()))
                    relations += 1;
            }

            for (int i = 0; i < right.getChildCount(); i++) {
                Attribute relation = ((Attribute) right.getChildAt(i)).getInRelationWith();

                if (relation != null && relation.getParent().getName().equals(left.getName())) {
                    query.append(((Attribute) right.getChildAt(i)).getName()).append(" = ? ");
                    relations -= 1;
                    if (relations != 0) query.append("AND ");
                }
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

            relations = 1;

            for (int i = 0; i < right.getChildCount(); i++) {
                Attribute relation = ((Attribute) right.getChildAt(i)).getInRelationWith();
                if (relation != null && relation.getParent().getName().equals(left.getName())) {
                    preparedStatement.setObject(relations, selection.getObject(relation.getName()));
                    relations += 1;
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Row row = new Row();
                row.setName(right.getName());

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                    row.addField(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));

                data.add(row);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
        return data;
    }

    @Override
    public List<Row> rightJoin(Entity left, Entity right, Row selection) {
        List<Row> data = new ArrayList<>();
        try {
            initConnection();

            StringBuilder query = new StringBuilder("SELECT ");

            for (int i = 0; i < right.getChildCount(); i++) {
                query.append(((DBNode) right.getChildAt(i)).getName());
                if (i != right.getChildCount() - 1) query.append(", ");
            }

            query.append(" FROM ").append(right).append(" WHERE ");

            int relations = 0;

            for (int i = 0; i < left.getChildCount(); i++) {
                Attribute relation = ((Attribute) left.getChildAt(i)).getInRelationWith();

                if (relation != null && relation.getParent().getName().equals(right.getName()))
                    relations += 1;
            }

            for (int i = 0; i < left.getChildCount(); i++) {
                Attribute relation = ((Attribute) left.getChildAt(i)).getInRelationWith();

                if (relation != null && relation.getParent().getName().equals(right.getName())) {
                    query.append(relation.getName()).append(" = ? ");
                    relations -= 1;

                    if (relations != 0)
                        query.append("AND ");
                }
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

            relations = 1;

            for (int i = 0; i < left.getChildCount(); i++) {
                Attribute relation = ((Attribute) left.getChildAt(i)).getInRelationWith();

                if (relation != null && relation.getParent().getName().equals(right.getName())) {
                    preparedStatement.setObject(relations, selection.getObject(((Attribute) left.getChildAt(i)).getName()));
                    relations += 1;
                }
            }

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {

                Row row = new Row();
                row.setName(right.getName());

                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
                    row.addField(rs.getMetaData().getColumnName(i), rs.getString(i));

                data.add(row);

            }
        } catch (Exception ex) {
            if (ex instanceof SQLException) throw new RuntimeException(ex);
            else ex.printStackTrace();
        } finally {
            closeConnection();
        }
        return data;
    }

    @Override
    public List<Row> search(Entity entity, String query) {
        List<Row> data = new ArrayList<>();
        try {
            initConnection();
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM ");
            sb.append(entity.getName());
            sb.append(" WHERE ");
            sb.append(query);

            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Row row = new Row();
                row.setName(entity.getName());

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                    row.addField(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));

                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return data;
    }

    @Override
    public List<Row> report(Entity entity, Attribute column, String aggregator, List<String> group) {
        List<Row> data = new ArrayList<>();
        try {
            initConnection();
            StringBuilder sb = new StringBuilder();

            sb.append("SELECT ");
            sb.append(aggregator);
            sb.append('(');
            sb.append(column.getName());
            sb.append(')');
            sb.append(" AS ");
            sb.append(aggregator.toLowerCase());
            sb.append("_");
            sb.append(column.getName());
            sb.append(", ");
            sb.append(group.get(0));
            sb.append(" FROM ");
            sb.append(entity.getName());
            sb.append(" GROUP BY ");
            for (int i = 0; i < group.size(); i++) {
                String attr = group.get(i);
                sb.append(attr);

                if (i < group.size() - 1)
                    sb.append(", ");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Row row = new Row();
                row.setName(entity.getName());

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                    row.addField(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));

                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return data;
    }
}
