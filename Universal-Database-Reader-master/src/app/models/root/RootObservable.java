package app.models.root;

import java.util.List;

import app.models.entity.Entity;
import app.resource.Row;

interface RootObservable {

    void addObserver(RootListener listener);
    void removeObserver(RootListener listener);

    void notifyEntitySelected(Entity entity);
    void notifyRelationsAdded(Entity origin, List<Row> data);
    void notifyClear();
}
