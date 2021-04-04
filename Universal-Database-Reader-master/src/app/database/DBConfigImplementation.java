package app.database;

import java.util.HashMap;
import java.util.Map;

public class DBConfigImplementation implements DBConfig {

    private Map<String, Object> parameters = new HashMap<>();

    @Override
    public Object getParameter(String parameter) {
        return this.parameters.get(parameter);
    }

    @Override
    public void addParameter(String parameter, Object value) {
        this.parameters.put(parameter, value);
    }
}
