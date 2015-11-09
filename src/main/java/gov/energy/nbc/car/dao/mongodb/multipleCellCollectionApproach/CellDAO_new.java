package gov.energy.nbc.car.dao.mongodb.multipleCellCollectionApproach;


import com.mongodb.BasicDBObject;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.ICellDAO;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.common.Row;
import gov.energy.nbc.car.model.document.CellDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class CellDAO_new extends DAO implements ICellDAO {

    private static final String PREFIX_FOR_CELL_COLLECTIONS = "CELL_";

    public CellDAO_new(String collectionName, ISettings settings) {

        super(PREFIX_FOR_CELL_COLLECTIONS + collectionName, settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }

    @Override
    public List<ObjectId> add(ObjectId rowId, Row row) {

        List<ObjectId> cellIDs = new ArrayList();

        for (String columnName : row.getColumnNames()) {

            Object value = row.get(columnName);

            ObjectId cellID = add(rowId, value);
            cellIDs.add(cellID);
        }

        return cellIDs;

    }

    public ObjectId add(ObjectId rowId, Object value) {

        CellDocument cellDocument = new CellDocument(rowId, getCollectionName(), value);
        return insert(cellDocument);
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new CellDocument(document);
    }

    @Override
    public DeleteResults deleteCellsAssociatedWithRow(ObjectId rowId) {

        DeleteResults allDeleteResults = new DeleteResults();

        Document rowIdFilter = new Document().
                append(CellDocument.ATTR_KEY__ROW_ID, rowId);

        DeleteResult deleteResult = getCollection().deleteMany(rowIdFilter);

        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }


    protected void makeSureTableColumnsIRelyUponAreIndexed() {

//        if (collectionExists(getCollectionName()) == false) {

            MongoCollection<Document> collection = getCollection();

            collection.createIndex(new BasicDBObject(CellDocument.ATTR_KEY__ROW_ID, 1));

            collection.createIndex(new BasicDBObject(CellDocument.ATTR_KEY__VALUE, 1));

            BasicDBObject compoundIndex = new BasicDBObject();
            compoundIndex.put(CellDocument.ATTR_KEY__ROW_ID, 1);
            compoundIndex.put(CellDocument.ATTR_KEY__VALUE, 1);
            collection.createIndex(compoundIndex);
//        }
    }

    public boolean collectionExists(final String collectionName) {

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "Called collectionExists()", true);
        ListCollectionsIterable<Document> collections = database.listCollections();

        for (final Document collection : collections) {

            if (collection.get("name").equals(collectionName)) {
                performanceLogger.done();
                return true;
            }
        }

        performanceLogger.done();
        return false;
    }
}
