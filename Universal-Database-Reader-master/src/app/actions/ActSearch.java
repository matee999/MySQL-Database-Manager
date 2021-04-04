package app.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.models.entity.Entity;
import app.views.dialogs.FilterAndSortDialog;
import app.views.dialogs.SearchDialog;


public class ActSearch extends AbstractAction {

    private Entity entity;

    public ActSearch(Entity entity) {
        this.entity = entity;

        putValue(NAME, "Search");
        putValue(SHORT_DESCRIPTION, "Search the active table");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SearchDialog dialog = new SearchDialog(entity);
        dialog.setVisible(true);
    }
}
