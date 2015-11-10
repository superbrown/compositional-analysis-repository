package gov.energy.nbc.car.model.mongodb.document;

import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.model.mongodb.AbstractDocument;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.model.IStoredFile;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import org.bson.Document;

import java.util.Date;
import java.util.List;

public class DatasetDocument extends AbstractDocument implements IDatasetDocument {

    public static final String ATTR_KEY__METADATA = "metadata";

    public DatasetDocument(String json) {
        super(json);
    }

    public DatasetDocument(Document object) {
        super(object);
    }

    public DatasetDocument(IMetadata metadata) {

        init(metadata);
    }

    public DatasetDocument(String dataCategory,
                           Date submissionDate,
                           String submitter,
                           String chargeNumber,
                           String projectName,
                           String comments,
                           IStoredFile uploadedFile,
                           List<IStoredFile> attachments) {

        IMetadata metadata = new Metadata(
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

        IMetadata metadata = new Metadata(DAOUtilities.serialize(document.get(ATTR_KEY__METADATA)));

        init(metadata);
    }

    private void init(IMetadata metadata) {

        put(ATTR_KEY__METADATA, metadata);
    }

    @Override
    public Metadata getMetadata() {
        return (Metadata) get(ATTR_KEY__METADATA);
    }

    @Override
    public String getDataCategory() {
        return (String) getMetadata().get(Metadata.ATTR_KEY__SAMPLE_TYPE);
    }
}
