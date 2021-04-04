package app.views.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import app.models.Attribute;
import app.models.DBNode;
import app.models.entity.Entity;
import app.resource.Row;
import app.views.MainFrame;

public class ReportDialog extends JDialog {

    private Entity entity;

    private int step = 1;

    public ReportDialog(Entity entity) {
        this.entity = entity;

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(450, 200));

        JPanel firstPanel = new JPanel(new GridLayout(0, 2));
        JComboBox<DBNode> colsBox = new JComboBox(new DefaultComboBoxModel(entity.getChildren().toArray()));
        JLabel colLabel = new JLabel("Column: ");
        firstPanel.add(colLabel);

        firstPanel.add(colsBox);
        JLabel aggregatorLabel = new JLabel("Aggregator: ");
        firstPanel.add(aggregatorLabel);
        JComboBox<String> aggregatorBox = new JComboBox(new String[]{"COUNT", "AVG"});
        firstPanel.add(aggregatorBox);
        add(firstPanel, BorderLayout.CENTER);

        JPanel secondPanel = new JPanel(new GridLayout(entity.getChildCount(), 1));
        secondPanel.add(new JLabel("Group by: "));
        secondPanel.add(new JLabel("Selected aggregator: " + aggregatorBox.getSelectedItem()));

        ArrayList<JCheckBox> groupBys = new ArrayList<>();

        for (DBNode attribute : entity.getChildren()) {
            JCheckBox groupBy = new JCheckBox(attribute.getName(), true);
            secondPanel.add(groupBy);
            groupBys.add(groupBy);
        }

        JButton confirm = new JButton("Next");
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (step == 1) {
                    remove(firstPanel);
                    confirm.setText("Confirm");
                    add(secondPanel);
                    pack();
                    validate();
                    repaint();
                } else {
                    List<String> groups = new ArrayList<>();
                    for (JCheckBox checkBox : groupBys)
                        if (checkBox.isSelected())
                            groups.add(checkBox.getText());

                    List<Row> data = MainFrame.getInstance().getDatabase().report(
                            entity,
                            (Attribute) colsBox.getSelectedItem(),
                            (String) aggregatorBox.getSelectedItem(),
                            groups
                    );

                    entity.notifyDataChanged(data);
                    ReportDialog.this.setVisible(false);
                }
                step += 1;
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReportDialog.this.setVisible(false);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(cancel);
        bottomPanel.add(confirm);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
