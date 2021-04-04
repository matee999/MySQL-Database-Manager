package app.database;

import java.util.HashMap;
import java.util.List;

import app.models.Attribute;
import app.models.entity.Entity;
import app.resource.Row;
import app.models.root.RootNode;

public class DatabaseImplementation implements Database {

	private Repository repository;
	
	public DatabaseImplementation(Repository repository) {
		this.repository = repository;
	}

	@Override
	public RootNode initRoot() {
		return repository.getSchema();
	}

	@Override
	public List<Row> readDataFromTable(Entity entity) {
		return repository.get(entity);
	}

	@Override
	public void insert(Entity entity, Row values) {
		repository.insert(entity, values);
	}

	@Override
	public void delete(Entity entity, HashMap<Attribute, String> values) {
		repository.delete(entity, values);
	}

	@Override
	public List<Row> filterAndSort(Entity origin, List<Attribute> columns, List<Boolean> filters, List<Boolean> sorts, List<Boolean> descendings) {
		return repository.filterAndSort(origin, columns, filters, sorts, descendings);
	}

	@Override
	public List<Row> leftJoin(Entity left, Entity right, Row selection) {
		return repository.leftJoin(left, right, selection);
	}

	@Override
	public List<Row> rightJoin(Entity left, Entity right, Row selection) {
		return repository.rightJoin(left, right, selection);
	}

	@Override
	public List<Row> search(Entity entity, String query) {
		return repository.search(entity, query);
	}

	@Override
	public List<Row> report(Entity entity, Attribute column, String aggregator, List<String> group) {
		return repository.report(entity, column, aggregator, group);
	}
}
