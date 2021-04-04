package app.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import app.models.entity.Entity;
import app.models.root.RootListener;
import app.models.root.RootNode;

public class EntitiesView extends JPanel implements RootListener {

    private JTabbedPane tabs;

    public EntitiesView(RootNode root) {
        root.addObserver(this);
        initialise();
    }

    private void initialise() {
        setLayout(new BorderLayout());

        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                MainFrame.getInstance().getRoot().notifyEntitySelected(
                        ((EntityView) tabs.getSelectedComponent()).getEntity()
                );
            }
        });
        add(tabs, BorderLayout.CENTER);
    }

    @Override
    public void onEntitySelected(Entity entity) {
        EntityView view = null;
        for (int i = 0; i < tabs.getComponents().length; i++) {
            if (((EntityView) tabs.getComponents()[i]).getEntity() == entity)
                view = (EntityView) tabs.getComponents()[i];
        }

        // Ako tab postoji, postavi ga u fokus
        if (view != null)
            tabs.setSelectedComponent(view);
            // Ako ne postoji, napravi novi tab
        else {
            EntityView documentView = new EntityView(entity, MainFrame.getInstance().getDatabase().readDataFromTable(entity), true);
            tabs.add(entity.getName(), documentView);
            tabs.setSelectedComponent(documentView);

            revalidate();
            repaint();
        }
    }
}
