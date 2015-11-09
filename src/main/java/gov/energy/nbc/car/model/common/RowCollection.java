package gov.energy.nbc.car.model.common;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RowCollection extends ArrayList<Row> {

    public RowCollection(List<List> datasetContent) {

        super();

        List<String> columnNames = datasetContent.get(0);
        List<List> rows = datasetContent.subList(1, datasetContent.size());

        init(columnNames, rows);
    }

    public RowCollection(List<String> columnNames, List<List> rows) {

        super();

        init(columnNames, rows);
    }

    private void init(List<String> columnNames, List<List> rows) {

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {

            List<Object> rowValues = rows.get(rowIndex);
            Row row = new Row(columnNames, rowIndex, rowValues);
            add(row);
        }
    }

    public RowCollection(String json) {

        init(json);
    }

    private void init(String json) {

        Object parsedJson = DAOUtilities.parse(json);

        BasicDBList parseredJson = (BasicDBList) parsedJson;

        for (Object o : parseredJson) {

            BasicDBObject basicDBObject = (BasicDBObject)o;
            Row row = new Row(basicDBObject);
            add(row);
        }
    }

    public Set getColumnNames() {
        return get(0).keySet();
    }
}
