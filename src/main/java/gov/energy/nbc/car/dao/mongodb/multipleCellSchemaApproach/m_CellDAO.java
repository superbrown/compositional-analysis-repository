package gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach;


import com.mongodb.BasicDBObject;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.dao.ICellDAO;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.IRow;
import gov.energy.nbc.car.model.mongodb.document.CellDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class m_CellDAO extends DAO implements ICellDAO {

    public m_CellDAO(String columnName, ISettings settings) {

        super(columnName, settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }

    @Override
    public List<ObjectId> add(ObjectId rowId, IRow row) {

        List<ObjectId> cellIDs = new ArrayList();

        for (String columnName : row.getColumnNames()) {

            Object value = row.getValue(columnName);

            ObjectId cellID = add(rowId, value);
            cellIDs.add(cellID);
        }

        return cellIDs;

    }

    public ObjectId add(ObjectId rowId, Object value) {

        CellDocument cellDocument = new CellDocument(rowId, getCollectionName(), value);
        return add(cellDocument);
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new CellDocument(document);
    }

    @Override
    public IDeleteResults deleteCellsAssociatedWithRow(ObjectId rowId) {

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
