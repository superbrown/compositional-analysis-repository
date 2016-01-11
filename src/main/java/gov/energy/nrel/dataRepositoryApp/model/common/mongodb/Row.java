package gov.energy.nrel.dataRepositoryApp.model.common.mongodb;

import gov.energy.nrel.dataRepositoryApp.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nrel.dataRepositoryApp.model.common.IRow;
import org.bson.Document;

import java.util.List;
import java.util.Set;

public class Row extends AbstractDocument implements IRow {

    public static final String MONGO_KEY__ROW_NUMBER = " Row";

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

        put(MongoFieldNameEncoder.toMongoSafeFieldName(MONGO_KEY__ROW_NUMBER), rowIndex);

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {

            String columnName = columnNames.get(columnIndex);
            Object columnValue = rowValues.get(columnIndex);

            put(MongoFieldNameEncoder.toMongoSafeFieldName(columnName), columnValue);
        }
    }

    @Override
    public Set<String> getColumnNames() {

        return this.keySet();
    }

    @Override
    public Object getValue(String columnName) {

        return this.get(columnName);
    }
}
