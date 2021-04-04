package app.models.entity;

import java.util.List;

import app.resource.Row;

public interface EntityListener {

    void onDataChanged(List<Row> data);
}
