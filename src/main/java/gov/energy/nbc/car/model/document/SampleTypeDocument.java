package gov.energy.nbc.car.model.document;

import gov.energy.nbc.car.model.AbstractDocument;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
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

    public SampleTypeDocument(Document document) {
        super(document);
    }

    protected void init(Document document) {

        if (document == null) {
            return;
        }

        initializeId(document);

        String sampleType = (String) document.get(ATTRIBUTE_KEY__SAMPLE_TYPE);

        List columnNames = (List) document.get(ATTRIBUTE_KEY__COLUMN_NAMES);;

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
