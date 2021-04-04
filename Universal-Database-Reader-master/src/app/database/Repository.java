package app.database;

import java.util.HashMap;
import java.util.List;

import app.models.Attribute;
import app.models.entity.Entity;
import app.resource.Row;
import app.models.root.RootNode;

public interface Repository {

    RootNode getSchema();

    List<Row> get(Entity entity);

    void insert(Entity entity, Row values);

    void delete(Entity entity, HashMap<Attribute, String> values);

    List<Row> filterAndSort(Entity origin, List<Attribute> columns, List<Boolean> filters, List<Boolean> sorts, List<Boolean> descendings);

    List<Row> leftJoin(Entity left, Entity right, Row selection);

    List<Row> rightJoin(Entity left, Entity right, Row selection);

    List<Row> search(Entity entity, String query);

    List<Row> report(Entity entity, Attribute column, String aggregator, List<String> group);
}
