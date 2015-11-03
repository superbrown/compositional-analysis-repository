package gov.energy.nbc.car.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.model.common.Data;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.common.SpreadsheetRow;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import gov.energy.nbc.car.model.document.SpreadsheetRowDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
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

    public List<ObjectId> add(ObjectId spreadsheetObjectId, SpreadsheetDocument spreadsheetDocument, Data data) {

        List<ObjectId> spreadsheetRowObjectIds = new ArrayList();

        Metadata metadata = spreadsheetDocument.getMetadata();

        for (SpreadsheetRow spreadsheetRow : data) {

            PerformanceLogger performanceLogger = new PerformanceLogger("new SpreadsheetRowDocument()");
            SpreadsheetRowDocument spreadsheetRowDocument = new SpreadsheetRowDocument(
                    spreadsheetObjectId,
                    metadata,
                    spreadsheetRow);
            performanceLogger.done();

            performanceLogger = new PerformanceLogger("insert(spreadsheetRowDocument)");
            ObjectId objectId = insert(spreadsheetRowDocument);
            performanceLogger.done();

            spreadsheetRowObjectIds.add(objectId);
        }

        return spreadsheetRowObjectIds;
    }

    public DeleteResults deleteRowsAssociatedWithSpreadsheet(ObjectId objectId) {

        Document spreadsheetIdFilter = new Document().
                append(SpreadsheetRowDocument.ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID, objectId);

        DeleteResult deleteResult = getCollection().deleteMany(spreadsheetIdFilter);

        DeleteResults allDeleteResults = new DeleteResults();
        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new SpreadsheetRowDocument(document);
    }

    @Override
    public DeleteResults delete(ObjectId objectId) {

        throw new RuntimeException(
                "This method should not be called because rows should not be deleted  independently of " +
                        "their spreasheet.");
    }

    public List<SpreadsheetRowDocument> executeQuery(String query) {

        Bson bson = (Bson)DAOUtilities.parse(query);
        List<Document> documents = query(bson);

        List <SpreadsheetRowDocument> spreadsheetRows = new ArrayList();

        for (Document document : documents) {

            SpreadsheetRowDocument spreadsheetRow = new SpreadsheetRowDocument(DAOUtilities.serialize(document));
            spreadsheetRows.add(spreadsheetRow);
        }

        return spreadsheetRows;
    }
}
