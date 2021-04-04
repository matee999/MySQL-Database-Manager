package app.views.dialogs;

import app.models.Attribute;
import app.models.AttributeConstraint;
import app.models.DBNode;
import app.models.entity.Entity;
import app.resource.AttributeType;
import app.resource.ConstraintType;
import app.resource.Row;
import app.views.MainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AddDialog extends JDialog {

    private Entity entity;
    private List<Row> rows;

    public AddDialog(Entity entity, List<Row> rows) {
        this.entity = entity;
        this.rows = rows;

        initialize();
    }

    public void initialize() {
        setTitle("Add");
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(600, 300));

        JButton addBut = new JButton("Add");
        JButton cancelBut = new JButton("Cancel");

        cancelBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddDialog.this.setVisible(false);
            }
        });

        JPanel centerPanel = new JPanel(new GridLayout(entity.getChildCount(), 2));
        ArrayList<JTextArea> inputs = new ArrayList<>();

        for (DBNode attribute : entity.getChildren()) {
            JLabel text = new JLabel(attribute.getName());
            centerPanel.add(text);
            JTextArea textToAdd = new JTextArea();
            centerPanel.add(textToAdd);
            inputs.add(textToAdd);
        }
        add(centerPanel);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));

        bottomPanel.add(addBut);
        bottomPanel.add(cancelBut);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        addBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Row newRow = new Row();
                    for (int i = 0; i < entity.getChildCount(); i++) {
                        Attribute attribute = (Attribute) entity.getChildAt(i);

                        for (int j = 0; j < attribute.getChildCount(); j++) {
                            AttributeConstraint constraint = (AttributeConstraint) attribute.getChildAt(j);

                            if (constraint.getConstraintType() == ConstraintType.PRIMARY_KEY || constraint.getConstraintType() == ConstraintType.NOT_NULL) {
                                if (inputs.get(i).getText().trim().equals("")) {
                                    AddDialog.this.setVisible(false);
                                    JOptionPane.showMessageDialog(null, "Polje ne moze ostati prazno!",
                                            "Greska", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }
                            if (constraint.getConstraintType() == ConstraintType.PRIMARY_KEY) {
                                for (Row row : rows) {
                                    if (inputs.get(i).getText().equals(row.getFields().get(attribute.getName()))) {
                                        AddDialog.this.setVisible(false);
                                        JOptionPane.showMessageDialog(null, "Polje sa unetim primarnim kljucem vec postoji!",
                                                "Greska", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                }
                            }
                        }

                        if (attribute.getAttributeType() == AttributeType.INT || attribute.getAttributeType() == AttributeType.BIGINT) {
                            try {
                                Integer.parseInt(inputs.get(i).getText());
                            } catch (NumberFormatException ex) {
                                AddDialog.this.setVisible(false);
                                JOptionPane.showMessageDialog(null, "Uneta vrednost u polje nije u korektnom obliku!",
                                        "Greska", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        if (attribute.getAttributeType() == AttributeType.DECIMAL || attribute.getAttributeType() == AttributeType.FLOAT) {
                            try {
                                Double.parseDouble(inputs.get(i).getText());
                            } catch (NumberFormatException ex) {
                                AddDialog.this.setVisible(false);
                                JOptionPane.showMessageDialog(null, "Uneta vrednost u polje nije u korektnom obliku!",
                                        "Greska", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        if (attribute.getAttributeType() == AttributeType.NUMERIC) {
                            double value = Double.parseDouble(inputs.get(i).getText());
                            newRow.addField(attribute.getName(), value);
                        } else
                            newRow.addField(attribute.getName(), inputs.get(i).getText());
                    }

                    MainFrame.getInstance().getDatabase().insert(entity, newRow);
                    entity.notifyDataChanged(MainFrame.getInstance().getDatabase().readDataFromTable(entity));

                    AddDialog.this.setVisible(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
}

