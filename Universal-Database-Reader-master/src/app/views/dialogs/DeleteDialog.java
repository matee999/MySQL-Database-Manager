package app.views.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import app.models.Attribute;
import app.models.AttributeConstraint;
import app.models.DBNode;
import app.models.entity.Entity;
import app.resource.ConstraintType;
import app.views.MainFrame;

public class DeleteDialog extends JDialog {

    private Entity entity;

    public DeleteDialog(Entity entity) {
        this.entity = entity;

        initialize();
    }

    private void initialize() {
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(450, 200));
        setLayout(new BorderLayout());

        List<Attribute> pkAttributes = new ArrayList<>();
        List<JTextArea> textAreas = new ArrayList<>();

        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<Attribute, String> values = new HashMap<>();
                for (int i = 0; i < pkAttributes.size(); i++) {
                    if (textAreas.get(i).getText().trim().length() == 0) {
                        JOptionPane.showMessageDialog(null, "Polje ne moze ostati prazno!",
                                "Greska", JOptionPane.ERROR_MESSAGE);

                        return;
                    }
                    values.put(pkAttributes.get(i), textAreas.get(i).getText());
                }
                MainFrame.getInstance().getDatabase().delete(entity, values);
                entity.notifyDataChanged(MainFrame.getInstance().getDatabase().readDataFromTable(entity));
                DeleteDialog.this.setVisible(false);
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteDialog.this.setVisible(false);
            }
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 2));

        for (DBNode attr : entity.getChildren()) {
            for (DBNode constraintNode : ((Attribute) attr).getChildren()) {
                AttributeConstraint constraint = (AttributeConstraint) constraintNode;
                if (constraint.getConstraintType() == ConstraintType.PRIMARY_KEY) {
                    pkAttributes.add((Attribute) attr);
                    textAreas.add(new JTextArea());
                    break;
                }
            }
        }
        for (int i = 0; i < pkAttributes.size(); i++) {
            centerPanel.add(new JLabel(pkAttributes.get(i).getName()));
            centerPanel.add(textAreas.get(i));
        }

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(cancel);
        bottomPanel.add(confirm);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
