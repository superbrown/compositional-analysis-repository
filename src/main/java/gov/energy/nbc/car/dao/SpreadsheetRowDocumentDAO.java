package gov.energy.nbc.car.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.model.common.SpreadsheetRow;
import gov.energy.nbc.car.model.document.SpreadsheetRowDocument;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class SpreadsheetRowDocumentDAO extends DAO
{
    public static final String ATTRIBUTE_KEY__COLLECTION_NAME = "spreadsheetRow";

    public SpreadsheetRowDocumentDAO(Settings settings) {

        super(ATTRIBUTE_KEY__COLLECTION_NAME, settings);
    }

    public SpreadsheetRowDocument get(String id) {

        return (SpreadsheetRowDocument) queryForOneWithId(id);
    }

    public List<ObjectId> add(ObjectId spreadsheetObjectId, SpreadsheetDocument spreadsheetDocument) {

        List<ObjectId> spreadsheetRowObjectIds = new ArrayList();

        Metadata metadata = spreadsheetDocument.getMetadata();

        for (SpreadsheetRow spreadsheetRow : spreadsheetDocument.getData()) {

            SpreadsheetRowDocument spreadsheetRowDocument = new SpreadsheetRowDocument(
                    spreadsheetObjectId,
                    metadata,
                    spreadsheetRow);

            ObjectId objectId = insert(spreadsheetRowDocument);
            spreadsheetRowObjectIds.add(objectId);
        }

        return spreadsheetRowObjectIds;
    }

    public DeleteResults deleteRowsAssociatedWithSpreadsheet(ObjectId objectId) {

        Document spreadsheetIdFilter = new Document();
        spreadsheetIdFilter.put(SpreadsheetRowDocument.ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID, objectId);

        DeleteResult deleteResult = getCollection().deleteMany(spreadsheetIdFilter);

        DeleteResults allDeleteResults = new DeleteResults();
        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(String json) {

        return new SpreadsheetRowDocument(json);
    }

    @Override
    public DeleteResults delete(ObjectId objectId) {

        throw new RuntimeException(
                "This method should not be called because rows should not be deleted  independently of " +
                        "their spreasheet.");
    }

    public List<SpreadsheetRowDocument> executeQuery(String query) {

        Bson bson = (Bson)JSON.parse(query);
        List<Document> documents = query(bson);

        List <SpreadsheetRowDocument> spreadsheetRows = new ArrayList();

        for (Document document : documents) {

            SpreadsheetRowDocument spreadsheetRow = new SpreadsheetRowDocument(JSON.serialize(document));
            spreadsheetRows.add(spreadsheetRow);
        }

        return spreadsheetRows;
    }
}
