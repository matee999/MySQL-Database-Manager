package app.models.root;


import java.util.List;

import app.models.entity.Entity;
import app.resource.Row;

public interface RootListener {

    default void onEntitySelected(Entity entity) {}
    default void onRelationsAdded(Entity origin, List<Row> data) {}
    default void onClear() {}
}
