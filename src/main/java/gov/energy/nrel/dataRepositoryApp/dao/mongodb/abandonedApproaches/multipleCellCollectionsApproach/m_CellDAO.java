package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach;


import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.dao.ICellDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.dto.DeleteResults;
import gov.energy.nrel.dataRepositoryApp.model.common.IRow;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.CellDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class m_CellDAO extends AbsDAO implements ICellDAO {

    public m_CellDAO(String columnName, ISettings settings) {

        super(columnName, settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }

    public List<ObjectId> add(ObjectId rowId, IRow row) {

        List<ObjectId> idsOfCellsAdded = new ArrayList();

        for (String columnName : row.getColumnNames()) {

            Object value = row.getValue(columnName);

            ObjectId cellID = add(rowId, value);
            idsOfCellsAdded.add(cellID);
        }

        return idsOfCellsAdded;

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
                append(CellDocument.MONGO_KEY__ROW_ID, rowId);

        DeleteResult deleteResult = getCollection().deleteMany(rowIdFilter);

        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }


    protected void makeSureTableColumnsIRelyUponAreIndexed() {

        MongoCollection<Document> cellCollection = getCollection();

        cellCollection.createIndex(new BasicDBObject(CellDocument.MONGO_KEY__VALUE, 1));

        BasicDBObject compoundIndex = new BasicDBObject();
        compoundIndex.put(CellDocument.MONGO_KEY__ROW_ID, 1);
        compoundIndex.put(CellDocument.MONGO_KEY__VALUE, 1);
        cellCollection.createIndex(compoundIndex);
    }
}
