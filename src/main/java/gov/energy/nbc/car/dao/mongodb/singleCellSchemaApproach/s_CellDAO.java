package gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach;


import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.ICellDAO;
import gov.energy.nbc.car.dao.dto.DeleteResults;
import gov.energy.nbc.car.model.IRow;
import gov.energy.nbc.car.model.mongodb.document.CellDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class s_CellDAO extends DAO implements ICellDAO {

    public static final String COLLECTION_NAME = "cell";

    public s_CellDAO(String collectionName, Settings settings) {
        super(collectionName, settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }

    public s_CellDAO(Settings settings) {
        super(COLLECTION_NAME, settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }


    public List<ObjectId> add(ObjectId rowId, IRow row) {

        List<ObjectId> cellIDs = new ArrayList();

        for (String columnName : row.getColumnNames()) {

            Object value = row.getValue(columnName);

            CellDocument cellDocument = new CellDocument(rowId, columnName, value);

            ObjectId cellID = add(cellDocument);
            cellIDs.add(cellID);
        }

        return cellIDs;

    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new CellDocument(document);
    }

    public DeleteResults deleteCellsAssociatedWithRow(ObjectId rowId) {

        DeleteResults allDeleteResults = new DeleteResults();

        Document rowIdFilter = new Document().
                append(CellDocument.ATTR_KEY__ROW_ID, rowId);

        DeleteResult deleteResult = getCollection().deleteMany(rowIdFilter);

        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }


    private static boolean HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = false;

    protected void makeSureTableColumnsIRelyUponAreIndexed() {
        if (HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED == false) {

            getCollection().createIndex(new BasicDBObject(CellDocument.ATTR_KEY__ROW_ID, 1));
            getCollection().createIndex(new BasicDBObject(CellDocument.ATTR_KEY__COLUMN_NAME, 1));
            getCollection().createIndex(new BasicDBObject(CellDocument.ATTR_KEY__VALUE, 1));

            log.info("Row Indexes");
            ListIndexesIterable<Document> indexes = getCollection().listIndexes();
            for (Document index : indexes) {
                log.info(index.toJson());
            }

            HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = true;
        }
    }
}
