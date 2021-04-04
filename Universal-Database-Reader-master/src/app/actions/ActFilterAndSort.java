package app.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.models.entity.Entity;
import app.views.dialogs.FilterAndSortDialog;


public class ActFilterAndSort extends AbstractAction {

    private Entity entity;

    public ActFilterAndSort(Entity entity) {
        this.entity = entity;

        putValue(NAME, "Filter & Sort");
        putValue(SHORT_DESCRIPTION, "Filter and sorting options for active table");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FilterAndSortDialog dialog = new FilterAndSortDialog(entity);
        dialog.setVisible(true);
    }
}
