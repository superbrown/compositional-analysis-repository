package gov.energy.nbc.car.model.document;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.model.AbstractDocument;
import gov.energy.nbc.car.model.common.Data;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.common.StoredFile;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class SpreadsheetDocument extends AbstractDocument {

    public static final String ATTRIBUTE_KEY__METADATA = "metadata";
    public static final String ATTRIBUTE_KEY__DATA = "data";

    public SpreadsheetDocument(String json) {
        super(json);
    }

    public SpreadsheetDocument(Object object) {
        super(object);
    }

    public SpreadsheetDocument(Metadata metadata, Data data) {

        init(metadata, data);
    }

    public SpreadsheetDocument(String sampleType,
                               Date submissionDate,
                               String submitter,
                               String chargeNumber,
                               String projectName,
                               String comments,
                               StoredFile uploadedFile,
                               List<StoredFile> attachments,
                               List<List> spreadsheetContent) {

        Metadata metadata = new Metadata(
                sampleType,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                uploadedFile,
                attachments);

        Data data = new Data(spreadsheetContent);

        init(metadata, data);
    }

    public SpreadsheetDocument(String sampleType,
                               Date submissionDate,
                               String submitter,
                               String chargeNumber,
                               String projectName,
                               String comments,
                               StoredFile uploadedFile,
                               List<StoredFile> attachments,
                               Data data) {

        Metadata metadata = new Metadata(
                sampleType,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                uploadedFile,
                attachments);

        init(metadata, data);
    }

    protected void initWithJson(String json) {

        BasicDBObject parsedJson = (BasicDBObject) JSON.parse(json);

        initializeId(parsedJson);

        Metadata metadata = new Metadata(JSON.serialize(parsedJson.get(ATTRIBUTE_KEY__METADATA)));
        Data data = new Data(JSON.serialize(parsedJson.get(ATTRIBUTE_KEY__DATA)));

        init(metadata, data);
    }

    private void init(Metadata metadata, Data data) {

        put(ATTRIBUTE_KEY__METADATA, metadata);
        put(ATTRIBUTE_KEY__DATA, data);
    }

    public Metadata getMetadata() {
        return (Metadata) get(ATTRIBUTE_KEY__METADATA);
    }

    public Data getData() {
        return (Data) get(ATTRIBUTE_KEY__DATA);
    }

    public Set getColumnNames() {
        return getData().getColumnNames();
    }

    public String getSampleType() {
        return (String) getMetadata().get(Metadata.ATTRIBUTE_KEY__SAMPLE_TYPE);
    }
}
