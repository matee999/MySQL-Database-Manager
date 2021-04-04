package app.models.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.models.Attribute;
import app.models.DBNode;
import app.models.DBNodeComposite;
import app.resource.Row;

public class Entity extends DBNodeComposite implements EntityObservable {

	private List<EntityListener> listeners;

	private Set<Entity> relationsFrom;
	private Set<Entity> relationsTo;

	public Entity(String name, DBNode parent) {
		super(name, parent);

		relationsFrom = new HashSet<>();
		relationsTo = new HashSet<>();
	}

	public void addRelationTo(Entity ent) {
		relationsTo.add(ent);
	}

	public void addRelationFrom(Entity ent) {
		relationsFrom.add(ent);
	}

	@Override
	public void addChild(DBNode child) {
		if(child instanceof Attribute) {
			Attribute attribute = (Attribute) child;
			getChildren().add(attribute);
		}
	}

	public Set<Entity> getRelationsTo() {
		return relationsTo;
	}

	public Set<Entity> getRelationsFrom() {
		return relationsFrom;
	}

	@Override
	public void addObserver(EntityListener listener) {
		if(listener == null)
			return;

		if(this.listeners ==null)
			this.listeners = new ArrayList<>();

		if(this.listeners.contains(listener))
			return;

		this.listeners.add(listener);
	}

	@Override
	public void removeObserver(EntityListener listener) {
		if(listener == null)
			return;

		this.listeners.remove(listener);
	}

	@Override
	public void notifyDataChanged(List<Row> data) {
		if (this.listeners == null)
			return;

		for (EntityListener listener : listeners)
			listener.onDataChanged(data);
	}
}
