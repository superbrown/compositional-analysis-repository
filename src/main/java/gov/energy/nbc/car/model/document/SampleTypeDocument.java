package gov.energy.nbc.car.model.document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.model.AbstractDocument;

import java.util.HashSet;
import java.util.Set;

public class SampleTypeDocument extends AbstractDocument {

    public static final String ATTRIBUTE_KEY__SAMPLE_TYPE = "sampleType";
    public static final String ATTRIBUTE_KEY__COLUMN_NAMES = "columnNames";

    public SampleTypeDocument() {
        super();

        setColumnNames(new HashSet<String>());
    }

    public SampleTypeDocument(String json) {
        super(json);
    }

    public SampleTypeDocument(Object object) {
        super(object);
    }

    protected void initWithJson(String json) {

        BasicDBObject parsedJson = (BasicDBObject) JSON.parse(json);

        initializeId(parsedJson);

        String sampleType = (String) parsedJson.get(ATTRIBUTE_KEY__SAMPLE_TYPE);

        BasicDBList columnNames = (BasicDBList) parsedJson.get(ATTRIBUTE_KEY__COLUMN_NAMES);

        Set<String> columnNameSet = new HashSet<>();
        for (Object colomnName : columnNames) {
            columnNameSet.add((String) colomnName);
        }

        init(sampleType, columnNameSet);
    }

    protected void init(String sampleType, Set<String> columnNames) {

        setSampleType(sampleType);
        setColumnNames(columnNames);
    }

    public void setSampleType(String sampleType) {
        put(ATTRIBUTE_KEY__SAMPLE_TYPE, sampleType);
    }

    public String getSampleType() {
        return (String) get(ATTRIBUTE_KEY__SAMPLE_TYPE);
    }

    public void setColumnNames(Set<String> columnNames) {
        put(ATTRIBUTE_KEY__COLUMN_NAMES, columnNames);
    }

    public Set<String> getColumnNames() {
        Set columneNames = (Set) get(ATTRIBUTE_KEY__COLUMN_NAMES);
        return columneNames;
    }
}
