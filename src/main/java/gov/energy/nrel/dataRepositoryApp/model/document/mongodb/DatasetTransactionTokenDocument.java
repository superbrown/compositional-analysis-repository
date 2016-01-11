package gov.energy.nrel.dataRepositoryApp.model.document.mongodb;

import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.AbstractDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

public class DatasetTransactionTokenDocument extends AbstractDocument {

    public static final String MONGO_KEY__DATASET_ID = "datasetId";

    public DatasetTransactionTokenDocument(ObjectId datasetId) {

        init(datasetId);
    }

    public DatasetTransactionTokenDocument(Document document) {
        super(document);
    }

    protected void init(ObjectId datasetId) {

        this.put(MONGO_KEY__DATASET_ID, datasetId);
    }

    @Override
    protected void init(Document document) {

        if (document == null) {
            return;
        }

        initObjectId(document);

        ObjectId rowId = (ObjectId) document.get(MONGO_KEY__DATASET_ID);

        init(rowId);
    }
}
