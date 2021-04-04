package app.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Row {

	private String name;
	private Map<String, Object> fields;
	
	public Row() {
		this.fields = new HashMap<>();
	}
	
	public void addField(String fieldName, Object value) {
		this.fields.put(fieldName, value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public Set<String> getColumns() {
		return fields.keySet();
	}

	public Object getObject(String fieldName) {
    	return fields.get(fieldName);
    }
}
