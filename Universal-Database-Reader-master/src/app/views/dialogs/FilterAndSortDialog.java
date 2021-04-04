package app.views.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import app.models.Attribute;
import app.models.DBNode;
import app.models.entity.Entity;
import app.resource.Row;
import app.views.MainFrame;

public class FilterAndSortDialog extends JDialog {

    private Entity entity;

    public FilterAndSortDialog(Entity entity) {
        this.entity = entity;

        initialize();
    }

    public void initialize() {
        setTitle("Filter & Sort");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1000, 600));

        JButton confirm = new JButton("Confirm");
        JButton cancel = new JButton("Cancel");

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FilterAndSortDialog.this.setVisible(false);
            }
        });

        JPanel centerPanel = new JPanel(new GridLayout(entity.getChildCount(), 3));
        ArrayList<JCheckBox> filters = new ArrayList<>();
        ArrayList<JCheckBox> sorts = new ArrayList<>();
        ArrayList<JCheckBox> descendings = new ArrayList<>();

        for (DBNode attribute : entity.getChildren()) {
            JCheckBox visibilityBox = new JCheckBox("Column visible: " + attribute.getName(), true);
            JCheckBox sortingBox = new JCheckBox("Sort by column: " + attribute.getName(), false);
            JCheckBox orderBox = new JCheckBox("Order descending: " + attribute.getName(), false);
            centerPanel.add(visibilityBox);
            centerPanel.add(sortingBox);
            centerPanel.add(orderBox);

            filters.add(visibilityBox);
            sorts.add(sortingBox);
            descendings.add(orderBox);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(confirm);
        bottomPanel.add(cancel);

        add(bottomPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<Attribute> attributes = new ArrayList<>();
                    ArrayList<Boolean> filterList, sortList, descendingList;
                    filterList = new ArrayList<>();
                    sortList = new ArrayList<>();
                    descendingList = new ArrayList<>();

                    for (int i = 0; i < entity.getChildCount(); i++) {
                        attributes.add((Attribute) entity.getChildAt(i));
                        filterList.add(filters.get(i).isSelected());
                        sortList.add(sorts.get(i).isSelected());

                        descendingList.add(descendings.get(i).isSelected());
                    }
                    List<Row> data = MainFrame.getInstance().getDatabase().filterAndSort(entity, attributes, filterList, sortList, descendingList);
                    entity.notifyDataChanged(data);

                    FilterAndSortDialog.this.setVisible(false);
                } catch (Exception er) {
                    er.printStackTrace();
                }
            }
        });
    }
}
