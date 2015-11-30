package gov.energy.nbc.car.model.mongodb.document;

import com.mongodb.BasicDBList;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.model.IRow;
import gov.energy.nbc.car.model.IRowDocument;
import gov.energy.nbc.car.model.mongodb.AbstractDocument;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.common.Row;
import org.bson.Document;
import org.bson.types.ObjectId;

public class RowDocument_usingListStructure extends AbstractDocument implements IRowDocument {

    public static final String ATTR_KEY__DATASET_ID = "datasetId";
    public static final String ATTR_KEY__METADATA = "metadata";
    public static final String ATTR_KEY__DATA = "data";

    public RowDocument_usingListStructure(ObjectId datasetId, IMetadata metadata, IRow data) {

        init(datasetId, metadata, data);
    }

    public RowDocument_usingListStructure(Document object) {
        super(object);
    }

    public RowDocument_usingListStructure(String json) {
        super(json);
    }

    private void init(ObjectId datasetId, IMetadata metadata, IRow data) {

        BasicDBList dataList = new BasicDBList();


        Document dataDocument = (Document) data;
        for (String key : dataDocument.keySet()) {

            Object value = dataDocument.get(key);
            dataList.add(new Document(key, value));
        }

        put(ATTR_KEY__DATASET_ID, datasetId);
        put(ATTR_KEY__METADATA, metadata);
        put(ATTR_KEY__DATA, dataList);
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
