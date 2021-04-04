package app.views.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import app.models.Attribute;
import app.models.DBNode;
import app.models.entity.Entity;
import app.resource.AttributeType;
import app.resource.Row;
import app.views.MainFrame;

public class SearchDialog extends JDialog {

    private Entity entity;

    public SearchDialog(Entity entity) {
        this.entity = entity;

        initialize();
    }

    public void initialize() {
        setTitle("Search");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 150));

        JButton confirm = new JButton("Confirm");
        JButton add = new JButton("Add new parameter");
        JButton cancel = new JButton("Cancel");

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SearchDialog.this.setVisible(false);
            }
        });

        JPanel centerPanel = new JPanel(new GridLayout(0, 4));
        ArrayList<JComboBox<String>> cols = new ArrayList<>();
        ArrayList<JComboBox<String>> operators = new ArrayList<>();
        ArrayList<JTextArea> inputs = new ArrayList<>();
        ArrayList<JComboBox<String>> comparators = new ArrayList<>();

        JComboBox<String> colsBox = new JComboBox(new DefaultComboBoxModel(entity.getChildren().toArray()));
        cols.add(colsBox);
        JComboBox<String> opsBox = new JComboBox<>(new DefaultComboBoxModel());
        updateComboBox(((Attribute) colsBox.getSelectedItem()), opsBox);
        colsBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Attribute selectedAttr = ((Attribute) colsBox.getSelectedItem());
                updateComboBox(selectedAttr, opsBox);
            }
        });
        operators.add(opsBox);
        JTextArea textArea = new JTextArea();
        inputs.add(textArea);

        centerPanel.add(colsBox);
        centerPanel.add(opsBox);
        centerPanel.add(textArea);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(confirm);
        bottomPanel.add(add);
        bottomPanel.add(cancel);

        add(bottomPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringBuilder query = new StringBuilder();

                for (int i = 0; i < cols.size(); i ++) {
                    query.append(((DBNode)cols.get(i).getSelectedItem()).getName());
                    query.append(" ");
                    String operator = (String) operators.get(i).getSelectedItem();
                    query.append(operator);
                    query.append(" ");
                    if (operator.equals("LIKE")) {
                        query.append('\'');
                        query.append(inputs.get(i).getText());
                        query.append('\'');
                    } else
                        query.append(inputs.get(i).getText());

                    if (i < cols.size() - 1) {
                        query.append(" ");
                        query.append(comparators.get(i).getSelectedItem());
                        query.append(" ");
                    }
                }
                List<Row> data = MainFrame.getInstance().getDatabase().search(entity, query.toString());
                entity.notifyDataChanged(data);

                SearchDialog.this.setVisible(false);
            }
        });
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> compsBox = new JComboBox(new DefaultComboBoxModel(new String[]{"AND", "OR"}));
                comparators.add(compsBox);
                centerPanel.add(compsBox);

                JComboBox<String> colsBox = new JComboBox(new DefaultComboBoxModel(entity.getChildren().toArray()));
                cols.add(colsBox);
                JComboBox<String> opsBox = new JComboBox<>(new DefaultComboBoxModel());
                operators.add(opsBox);
                Attribute selectedAttr = ((Attribute) colsBox.getSelectedItem());
                updateComboBox(selectedAttr, opsBox);
                colsBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        Attribute selectedAttr = ((Attribute) colsBox.getSelectedItem());
                        updateComboBox(selectedAttr, opsBox);
                    }
                });
                JTextArea textArea = new JTextArea();

                inputs.add(textArea);

                centerPanel.add(colsBox);
                centerPanel.add(opsBox);
                centerPanel.add(textArea);

                SearchDialog.this.pack();
                SearchDialog.this.revalidate();
            }
        });
    }

    void updateComboBox(Attribute attribute, JComboBox<String> box) {
        AttributeType type = attribute.getAttributeType();

        ((DefaultComboBoxModel)box.getModel()).removeAllElements();
        if (type == AttributeType.NUMERIC || type == AttributeType.INT || type == AttributeType.BIGINT || type == AttributeType.FLOAT) {
            ((DefaultComboBoxModel)box.getModel()).addElement("<");
            ((DefaultComboBoxModel)box.getModel()).addElement(">");
            ((DefaultComboBoxModel)box.getModel()).addElement("=");
            ((DefaultComboBoxModel)box.getModel()).addElement(">=");
            ((DefaultComboBoxModel)box.getModel()).addElement("<=");
        } else {
            ((DefaultComboBoxModel)box.getModel()).addElement("LIKE");
        }
    }
}
