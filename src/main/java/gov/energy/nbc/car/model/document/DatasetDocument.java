package gov.energy.nbc.car.model.document;

import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.model.AbstractDocument;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.common.StoredFile;
import org.bson.Document;

import java.util.Date;
import java.util.List;

public class DatasetDocument extends AbstractDocument {

    public static final String ATTR_KEY__METADATA = "metadata";

    public DatasetDocument(String json) {
        super(json);
    }

    public DatasetDocument(Document object) {
        super(object);
    }

    public DatasetDocument(Metadata metadata) {

        init(metadata);
    }

    public DatasetDocument(String dataCategory,
                           Date submissionDate,
                           String submitter,
                           String chargeNumber,
                           String projectName,
                           String comments,
                           StoredFile uploadedFile,
                           List<StoredFile> attachments) {

        Metadata metadata = new Metadata(
                dataCategory,
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

        initObjectId(document);

        Metadata metadata = new Metadata(DAOUtilities.serialize(document.get(ATTR_KEY__METADATA)));

        init(metadata);
    }

    private void init(Metadata metadata) {

        put(ATTR_KEY__METADATA, metadata);
    }

    public Metadata getMetadata() {
        return (Metadata) get(ATTR_KEY__METADATA);
    }

    public String getDataCategory() {
        return (String) getMetadata().get(Metadata.ATTR_KEY__SAMPLE_TYPE);
    }
}
