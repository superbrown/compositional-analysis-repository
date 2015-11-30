package gov.energy.nbc.car.dao.mongodb.everthingInTheRowCollectionApproach;


import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.dao.ICellDAO;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.IRow;
import gov.energy.nbc.car.model.mongodb.document.CellDocument;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.settings.Settings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class r_CellDAO extends DAO implements ICellDAO {

    public static final String COLLECTION_NAME = "cell";

    public r_CellDAO(String collectionName, Settings settings) {
        super(collectionName, settings);
    }

    public r_CellDAO(ISettings settings) {
        super(COLLECTION_NAME, settings);
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

    public IDeleteResults deleteCellsAssociatedWithRow(ObjectId rowId) {

        DeleteResults allDeleteResults = new DeleteResults();

        Document rowIdFilter = new Document().
                append(CellDocument.ATTR_KEY__ROW_ID, rowId);

        DeleteResult deleteResult = getCollection().deleteMany(rowIdFilter);

        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {
        return new CellDocument(document);
    }
}
