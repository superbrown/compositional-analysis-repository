package gov.energy.nbc.car.model.common;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.model.AbstractBasicDBObject;

import java.util.List;

public class SpreadsheetRow extends AbstractBasicDBObject {

    public static final String ATTRIBUTE_KEY__ROW_NUMBER = "rowNumber";

    public SpreadsheetRow() {
        super();
    }

    public SpreadsheetRow(String json) {
        super(json);
    }

    public SpreadsheetRow(Object object) {
        super(object);
    }

    public SpreadsheetRow(List columnNames, int rowIndex, List rowValues) {

        put(ATTRIBUTE_KEY__ROW_NUMBER, rowIndex);

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {

            Object columnName = columnNames.get(columnIndex);
            Object columnValue = rowValues.get(columnIndex);
            put(columnName.toString(), columnValue);
        }
    }

    public void init(String json) {

        BasicDBObject parseredJson = (BasicDBObject) JSON.parse(json);

        for (String key : parseredJson.keySet()) {

            put(key, parseredJson.get(key));
        }
    }
}
