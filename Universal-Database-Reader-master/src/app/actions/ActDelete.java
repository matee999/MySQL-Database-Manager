package app.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.models.entity.Entity;
import app.views.dialogs.DeleteDialog;

public class ActDelete extends AbstractAction {

    private Entity entity;

    public ActDelete(Entity entity){
        this.entity = entity;

        putValue(NAME, "Delete");
        putValue(SHORT_DESCRIPTION, "Delete row from table");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        DeleteDialog dialog = new DeleteDialog(entity);
        dialog.setVisible(true);
    }
}
