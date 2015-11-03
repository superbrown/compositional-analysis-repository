package gov.energy.nbc.car.model.common;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.fileReader.MongoFieldNameEncoder;
import gov.energy.nbc.car.model.AbstractBasicDBObject;

import java.util.List;

public class SpreadsheetRow extends AbstractBasicDBObject {

    protected static MongoFieldNameEncoder mongoFieldNameEncoder = new MongoFieldNameEncoder();

    public static final String ATTRIBUTE_KEY__ROW_NUMBER = "_origDocRowNum";

    public SpreadsheetRow() {
        super();
    }

    public SpreadsheetRow(String json) {
        super(json);
    }

    public SpreadsheetRow(Object object) {
        super(object);
    }

    public SpreadsheetRow(List<String> columnNames, int rowIndex, List rowValues) {

        put(ATTRIBUTE_KEY__ROW_NUMBER, rowIndex);

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {

            String columnName = columnNames.get(columnIndex);
            String encodedColumnName = mongoFieldNameEncoder.encode(columnName);
            Object columnValue = rowValues.get(columnIndex);

            put(encodedColumnName, columnValue);
        }
    }

    public void init(String json) {

        BasicDBObject parseredJson = (BasicDBObject) DAOUtilities.parse(json);

        for (String key : parseredJson.keySet()) {

            String decodedColumnName = mongoFieldNameEncoder.decode(key);
            Object columnValue = parseredJson.get(key);
            put(decodedColumnName, columnValue);
        }
    }
}
