package app.models.root;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import app.models.DBNode;
import app.models.DBNodeComposite;
import app.models.entity.Entity;
import app.resource.Row;

public class RootNode extends DBNodeComposite implements TreeNode, RootObservable {

	private List<RootListener> listeners;

	public RootNode(String name) {
		super(name, null);
	}

	@Override
	public void addChild(DBNode child) {
		if (child instanceof Entity) {
			Entity entity =(Entity) child;

			getChildren().add(entity);
		}
	}

	@Override
	public void addObserver(RootListener listener) {
		if(listener == null)
			return;
		if(this.listeners ==null)
			this.listeners = new ArrayList<>();
		if(this.listeners.contains(listener))
			return;
		this.listeners.add(listener);
	}

	@Override
	public void removeObserver(RootListener listener) {
		if(listener == null)
			return;

		this.listeners.remove(listener);
	}

	@Override
	public void notifyEntitySelected(Entity entity) {
		if (this.listeners == null)
			return;

		for (RootListener listener : listeners)
			listener.onEntitySelected(entity);
	}

	@Override
	public void notifyRelationsAdded(Entity origin, List<Row> data) {
		if (this.listeners == null)
			return;

		for (RootListener listener : listeners)
			listener.onRelationsAdded(origin, data);
	}

	@Override
	public void notifyClear() {
		if (this.listeners == null)
			return;

		for (RootListener listener : listeners)
			listener.onClear();
	}
}
