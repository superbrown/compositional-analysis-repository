package gov.energy.nbc.car.model.document;

import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.model.AbstractDocument;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataCategoryDocument extends AbstractDocument {

    public static final String ATTR_KEY__SAMPLE_TYPE = "dataCategory";
    public static final String ATTR_KEY__COLUMN_NAMES = "columnNames";

    public DataCategoryDocument() {
        super();

        setColumnNames(new HashSet<String>());
    }

    public DataCategoryDocument(String json) {
        super(json);
    }

    public DataCategoryDocument(Document document) {
        super(document);
    }

    protected void init(Document document) {

        if (document == null) {
            return;
        }

        initObjectId(document);

        String dataCategory = (String) document.get(ATTR_KEY__SAMPLE_TYPE);

        List columnNames = (List) document.get(ATTR_KEY__COLUMN_NAMES);;

        Set<String> columnNameSet = new HashSet<>();
        for (Object colomnName : columnNames) {
            columnNameSet.add((String) colomnName);
        }

        init(dataCategory, columnNameSet);
    }

    protected void init(String dataCategory, Set<String> columnNames) {

        setDataCategory(dataCategory);
        setColumnNames(columnNames);
    }

    public void setDataCategory(String dataCategory) {
        put(ATTR_KEY__SAMPLE_TYPE, dataCategory);
    }

    public String getDataCategory() {
        return (String) get(ATTR_KEY__SAMPLE_TYPE);
    }

    public void setColumnNames(Set<String> columnNames) {
        put(ATTR_KEY__COLUMN_NAMES, columnNames);
    }

    public Set<String> getColumnNames() {

        Set columnNames = (Set) get(ATTR_KEY__COLUMN_NAMES);
        Set<String> decodedColumnNames = convertToClientSideFieldNames(columnNames);
        return decodedColumnNames;
    }

    protected Set<String> convertToClientSideFieldNames(Set<String> columnNames) {

        Set<String> decodedColumnNames = new HashSet<>();

        for (String columnName : columnNames) {

            decodedColumnNames.add(MongoFieldNameEncoder.toClientSideFieldName(columnName));
        }

        return decodedColumnNames;
    }
}
