package app.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.models.entity.Entity;
import app.views.dialogs.ReportDialog;
import app.views.dialogs.SearchDialog;


public class ActReport extends AbstractAction {

    private Entity entity;

    public ActReport(Entity entity) {
        this.entity = entity;

        putValue(NAME, "Report");
        putValue(SHORT_DESCRIPTION, "Get report for the active table");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ReportDialog dialog = new ReportDialog(entity);
        dialog.setVisible(true);
    }
}
