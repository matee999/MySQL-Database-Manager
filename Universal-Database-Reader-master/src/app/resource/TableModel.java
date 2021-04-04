package app.resource;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import app.resource.Row;

public class TableModel extends DefaultTableModel {

    private List<Row> rows;

	private void updateModel(){

	    if (rows.size() > 0) {
            int columnCount = rows.get(0).getFields().keySet().size();

            Vector columnVector = DefaultTableModel.convertToVector(rows.get(0).getFields().keySet().toArray());
            Vector dataVector = new Vector(columnCount);

            for (Row row : rows) {
                dataVector.add(DefaultTableModel.convertToVector(row.getFields().values().toArray()));
            }
            setDataVector(dataVector, columnVector);
        } else {
            setDataVector(new Vector<>(), new Vector<>());
        }
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;

        updateModel();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
