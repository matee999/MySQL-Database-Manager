package app.views;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import app.models.entity.Entity;
import app.models.root.RootListener;
import app.models.root.RootNode;
import app.resource.Row;

public class EntitiesHelperView extends JPanel implements RootListener {

    private JTabbedPane tabs;

    public EntitiesHelperView(RootNode root) {
        root.addObserver(this);
        initialise();
    }

    private void initialise() {
        setLayout(new BorderLayout());
        tabs = new JTabbedPane(JTabbedPane.TOP);

        add(tabs, BorderLayout.CENTER);
    }

    @Override
    public void onEntitySelected(Entity entity) {
        tabs.removeAll();

        List<Entity> connections = new ArrayList<>(entity.getRelationsFrom().size() + entity.getRelationsTo().size());
        connections.addAll(entity.getRelationsFrom());
        connections.addAll(entity.getRelationsTo());

        for (Entity connection : connections) {
            EntityView view = null;
            for (int i = 0; i < tabs.getComponents().length; i++) {
                if (((EntityView) tabs.getComponents()[i]).getEntity() == connection)
                    view = (EntityView) tabs.getComponents()[i];
            }

            // Ako tab postoji, postavi ga u fokus
            if (view != null)
                tabs.setSelectedComponent(view);
                // Ako ne postoji, napravi novi tab
            else {
                EntityView documentView = new EntityView(connection, MainFrame.getInstance().getDatabase().readDataFromTable(connection), false);
                tabs.add(connection.getName(), documentView);
                tabs.setSelectedComponent(documentView);
            }
        }

		revalidate();
		repaint();
    }

    @Override
    public void onRelationsAdded(Entity origin, List<Row> data) {
        EntityView documentView = new EntityView(origin, data, false);
        tabs.add(origin.getName(), documentView);
        tabs.setSelectedComponent(documentView);

        revalidate();
        repaint();
    }

    @Override
    public void onClear() {
        tabs.removeAll();
    }
}
