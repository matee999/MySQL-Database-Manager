package app.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import app.actions.ActAdd;
import app.actions.ActDelete;
import app.actions.ActFilterAndSort;
import app.actions.ActReport;
import app.actions.ActSearch;
import app.models.entity.Entity;
import app.models.entity.EntityListener;
import app.resource.Row;
import app.resource.TableModel;

public class EntityView extends JPanel implements EntityListener {

    private TableModel tableModel;
    private JTable table;
    private Entity entity;
    private List<Row> data;

    private boolean editable;

    public EntityView(Entity entity, List<Row> data, boolean editable) {
        this.entity = entity;
        this.entity.addObserver(this);
        this.editable = editable;
		this.tableModel = new TableModel();
		this.tableModel.setRows(data);
        this.data = data;

        initialise();
    }

    private void initialise() {
        setLayout(new BorderLayout());

        table = new JTable();
        table.setPreferredScrollableViewportSize(new Dimension(900, editable ? 225 : 250));
        table.setFillsViewportHeight(true);
        table.setModel(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        if (editable) {
            JButton butAdd = new JButton("ADD");
            butAdd.addActionListener(new ActAdd(entity, data));
            JButton butDelete = new JButton("DELETE");
            butDelete.addActionListener(new ActDelete(entity));
            JButton butFilterSort = new JButton("FILTER AND SORT");
            butFilterSort.addActionListener(new ActFilterAndSort(entity));
            JButton butSearch = new JButton("SEARCH");
            butSearch.addActionListener(new ActSearch(entity));
            JButton butReport = new JButton("REPORT");
            butReport.addActionListener(new ActReport(entity));

            JPanel buttons = new JPanel();

            buttons.add(butAdd);
            buttons.add(butDelete);
            buttons.add(butFilterSort);
            buttons.add(butSearch);
            buttons.add(butReport);

            add(buttons, BorderLayout.SOUTH);

            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (table.getSelectedRow() != -1) {
                        Row selection = new Row();
                        int c = table.getColumnCount();
                        for (int i = 0; i < c; i++) {
                            Object o = table.getValueAt(table.getSelectedRow(), i);
                            selection.addField(table.getColumnName(i), o);
                        }
                        Set<Entity> relationsTo = entity.getRelationsTo();
                        Set<Entity> relationsFrom = entity.getRelationsFrom();

						MainFrame.getInstance().getRoot().notifyClear();
                        for (Entity entityTo : relationsTo) {
                            List<Row> data = MainFrame.getInstance().getDatabase().leftJoin(entity, entityTo, selection);
                            MainFrame.getInstance().getRoot().notifyRelationsAdded(entityTo, data);
                        }
                        for (Entity entityFrom : relationsFrom) {
                            List<Row> data = MainFrame.getInstance().getDatabase().rightJoin(entity, entityFrom, selection);
							MainFrame.getInstance().getRoot().notifyRelationsAdded(entityFrom, data);
                        }
                    }
                }
            });
        }
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public void onDataChanged(List<Row> data) {
        tableModel.setRows(data);
    }
}
