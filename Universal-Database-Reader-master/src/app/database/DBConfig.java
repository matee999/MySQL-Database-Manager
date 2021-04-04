package app.database;

public interface DBConfig {

	Object getParameter(String parameter);

    void addParameter(String parameter, Object value);
}
