package gov.energy.nbc.car.model.mongodb.common;

import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.model.mongodb.AbstractDocument;
import gov.energy.nbc.car.model.IRow;
import org.bson.Document;

import java.util.List;
import java.util.Set;

public class Row extends AbstractDocument implements IRow {

    public static final String ATTR_KEY__ROW_NUMBER = "_origDocRowNum";

    public Row() {
        super();
    }

    public Row(String json) {
        super(json);
    }

    @Override
    protected void init(Document document) {

        initObjectId(document);

    }

    public Row(Object object) {
        super(object);
    }

    public Row(List<String> columnNames, int rowIndex, List rowValues) {

        put(ATTR_KEY__ROW_NUMBER, rowIndex);

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {

            String columnName = columnNames.get(columnIndex);
            String mongoSafeFieldName = MongoFieldNameEncoder.toMongoSafeFieldName(columnName);
            Object columnValue = rowValues.get(columnIndex);

            put(mongoSafeFieldName, columnValue);
        }
    }

//    public void init(String json) {
//
//        BasicDBObject parseredJson = (BasicDBObject) DAOUtilities.parse(json);
//
//        for (String key : parseredJson.keySet()) {
//
//            String decodedColumnName = MongoFieldNameEncoder.toClientSideFieldName(key);
//            Object columnValue = parseredJson.get(key);
//            put(decodedColumnName, columnValue);
//        }
//    }

    @Override
    public Set<String> getColumnNames() {

        return this.keySet();
    }

    @Override
    public Object getValue(String columnName) {

        return this.get(columnName);
    }
}
