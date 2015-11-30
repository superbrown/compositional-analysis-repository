package gov.energy.nbc.car.dao.mongodb.multipleCellCollectionsApproach;


import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.dao.ICellDAO;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.IRow;
import gov.energy.nbc.car.model.mongodb.document.CellDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class m_CellDAO extends DAO implements ICellDAO {

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
                append(CellDocument.ATTR_KEY__ROW_ID, rowId);

        DeleteResult deleteResult = getCollection().deleteMany(rowIdFilter);

        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }


    protected void makeSureTableColumnsIRelyUponAreIndexed() {

        MongoCollection<Document> cellCollection = getCollection();

        cellCollection.createIndex(new BasicDBObject(CellDocument.ATTR_KEY__VALUE, 1));

        BasicDBObject compoundIndex = new BasicDBObject();
        compoundIndex.put(CellDocument.ATTR_KEY__ROW_ID, 1);
        compoundIndex.put(CellDocument.ATTR_KEY__VALUE, 1);
        cellCollection.createIndex(compoundIndex);
    }
}
