package gov.energy.nrel.dataRepositoryApp.dao.mongodb;


import com.mongodb.BasicDBObject;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DatasetTransactionTokenDocument;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class DatasetTransactionTokenDAO extends AbsDAO implements gov.energy.nrel.dataRepositoryApp.dao.IDatasetTransactionTokenDAO {

    public static final String COLLECTION_NAME = "datasetTransactionToken";

    public DatasetTransactionTokenDAO(String collectionName, Settings settings) {
        super(collectionName, settings);
    }

    public DatasetTransactionTokenDAO(ISettings settings) {
        super(COLLECTION_NAME, settings);
    }

    @Override
    public void addToken(ObjectId datasetId) {

        // DESIGN NOTE: I don't know why we need to create a wrapper here, but if we don't, Mongo throws the
        // following exception:
        //
        // org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class
        // gov.energy.nbc.dataset.model.document.CellDocument.

        Document datasetTransactionTokenDocument = new Document(new DatasetTransactionTokenDocument(datasetId));
        add(datasetTransactionTokenDocument);
    }

    @Override
    public void removeToken(ObjectId datasetId) throws UnknownEntity {

        Document document = DAOUtilities.getOne(
                getCollection(),
                new BasicDBObject().append(DatasetTransactionTokenDocument.MONGO_KEY__DATASET_ID, datasetId));

        ObjectId objectId = (ObjectId) document.get(DatasetTransactionTokenDocument.MONGO_KEY__ID);
        delete(objectId);
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new DatasetTransactionTokenDocument(document);
    }


    @Override
    public List<ObjectId> getDatasetIdsOfAllTokens() {

        List<Document> documents = DAOUtilities.get(
                getCollection(),
                new BasicDBObject(),
                new BasicDBObject().append(DatasetTransactionTokenDocument.MONGO_KEY__DATASET_ID, 1));

        List<ObjectId> datasetIds = new ArrayList<>();
        for (Document document : documents) {

            datasetIds.add((ObjectId) document.get(DatasetTransactionTokenDocument.MONGO_KEY__DATASET_ID));
        }

        return datasetIds;
    }


    private static boolean HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = false;

    @Override
    protected void makeSureTableColumnsIRelyUponAreIndexed() {

        if (HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED == false) {

            getCollection().createIndex(new Document().append(DatasetTransactionTokenDocument.MONGO_KEY__DATASET_ID, 1));
            HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = true;
        }
    }
}
