package gov.energy.nrel.dataRepositoryApp.model.mongodb.document;

import gov.energy.nrel.dataRepositoryApp.model.IMetadata;
import gov.energy.nrel.dataRepositoryApp.model.IRow;
import gov.energy.nrel.dataRepositoryApp.model.IRowDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.AbstractDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Row;
import org.bson.Document;
import org.bson.types.ObjectId;

public class RowDocument extends AbstractDocument implements IRowDocument {

    public static final String ATTR_KEY__DATASET_ID = "datasetId";
    public static final String ATTR_KEY__METADATA = "metadata";
    public static final String ATTR_KEY__DATA = "data";

    public RowDocument(ObjectId datasetId, IMetadata metadata, IRow data) {

        init(datasetId, metadata, data);
    }

    public RowDocument(Document object) {
        super(object);
    }

    public RowDocument(String json) {
        super(json);
    }

    private void init(ObjectId datasetId, IMetadata metadata, IRow data) {

        put(ATTR_KEY__DATASET_ID, datasetId);
        put(ATTR_KEY__METADATA, metadata);
        put(ATTR_KEY__DATA, data);
    }

    protected void init(Document document) {

        if (document == null) {
            return;
        }

        initObjectId(document);

        ObjectId rowCollectionId = (ObjectId) document.get(ATTR_KEY__DATASET_ID);
        IMetadata metada = new Metadata((Document) document.get(ATTR_KEY__METADATA));
        IRow data = new Row();

        init(rowCollectionId, metada, data);
    }
}
