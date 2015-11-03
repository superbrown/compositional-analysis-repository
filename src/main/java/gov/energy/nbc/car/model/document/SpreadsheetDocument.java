package gov.energy.nbc.car.model.document;

import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.model.AbstractDocument;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.common.StoredFile;
import org.bson.Document;

import java.util.Date;
import java.util.List;

public class SpreadsheetDocument extends AbstractDocument {

    public static final String ATTRIBUTE_KEY__METADATA = "metadata";

    public SpreadsheetDocument(String json) {
        super(json);
    }

    public SpreadsheetDocument(Document object) {
        super(object);
    }

    public SpreadsheetDocument(Metadata metadata) {

        init(metadata);
    }

    public SpreadsheetDocument(String sampleType,
                               Date submissionDate,
                               String submitter,
                               String chargeNumber,
                               String projectName,
                               String comments,
                               StoredFile uploadedFile,
                               List<StoredFile> attachments) {

        Metadata metadata = new Metadata(
                sampleType,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                uploadedFile,
                attachments);

        init(metadata);
    }

    protected void init(Document document) {

        if (document == null) {
            return;
        }

        initializeId(document);

        Metadata metadata = new Metadata(DAOUtilities.serialize(document.get(ATTRIBUTE_KEY__METADATA)));

        init(metadata);
    }

    private void init(Metadata metadata) {

        put(ATTRIBUTE_KEY__METADATA, metadata);
    }

    public Metadata getMetadata() {
        return (Metadata) get(ATTRIBUTE_KEY__METADATA);
    }

    public String getSampleType() {
        return (String) getMetadata().get(Metadata.ATTRIBUTE_KEY__SAMPLE_TYPE);
    }
}
