package app.models.entity;

import java.util.List;

import app.resource.Row;

public interface EntityObservable {

    void addObserver(EntityListener listener);
    void removeObserver(EntityListener listener);

    void notifyDataChanged(List<Row> data);
}
