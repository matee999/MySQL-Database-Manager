package app.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import app.models.entity.Entity;
import app.resource.Row;
import app.views.dialogs.AddDialog;

public class ActAdd extends AbstractAction {

    private Entity entity;
    private List<Row> data;

    public ActAdd(Entity entity, List<Row> data) {
        this.entity = entity;
        this.data = data;

        putValue(NAME, "Insert");
        putValue(SHORT_DESCRIPTION, "Insert data in active table");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AddDialog dialog = new AddDialog(entity, data);
        dialog.setVisible(true);
    }
}
