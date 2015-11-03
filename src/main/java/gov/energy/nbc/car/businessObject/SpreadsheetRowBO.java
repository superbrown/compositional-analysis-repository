package gov.energy.nbc.car.businessObject;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.dao.SpreadsheetRowDocumentDAO;
import gov.energy.nbc.car.model.document.SpreadsheetRowDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

public class SpreadsheetRowBO {

    protected SpreadsheetRowDocumentDAO spreadsheetRowDocumentDAO;
    protected SpreadsheetRowDocumentDAO spreadsheetRowDocumentDAO_FOR_TESTING_PURPOSES;

    public SpreadsheetRowBO(Settings settings, Settings settings_forUnitTestingPurposes) {

        spreadsheetRowDocumentDAO = new SpreadsheetRowDocumentDAO(settings);
        spreadsheetRowDocumentDAO_FOR_TESTING_PURPOSES = new SpreadsheetRowDocumentDAO(settings_forUnitTestingPurposes);
    }

    public String getSpreadsheetRow(TestMode testMode, String spreadsheetRowId) {

        SpreadsheetRowDocument spreadsheetRowDocument = getSpreadsheetRowDAO(testMode).get(spreadsheetRowId);

        if (spreadsheetRowDocument == null) { return null; }

        String json = DAOUtilities.serialize(spreadsheetRowDocument);
        return json;
    }

    public String getSpreadsheetRows(TestMode testMode, String query) {

        List<SpreadsheetRowDocument> spreadsheetRowDocuments = getSpreadsheetRowDAO(testMode).executeQuery(query);

        if (spreadsheetRowDocuments.size() == 0) { return null; }

        String json = DAOUtilities.serialize(spreadsheetRowDocuments);
        return json;
    }

    public String getAllSpreadsheetRows(TestMode testMode) {

        FindIterable<Document> spreadsheetRowDocuments = getSpreadsheetRowDAO(testMode).getAll();

        String jsonOut = DAOUtilities.serialize(spreadsheetRowDocuments);
        return jsonOut;
    }


    public String getRowsForSpreadsheet(TestMode testMode, String speadSheetId) {

        Document idFilter = new Document().append(
                SpreadsheetRowDocument.ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID, new
                ObjectId(speadSheetId));

        PerformanceLogger performanceLogger = new PerformanceLogger("getSpreadsheetRowDAO(testMode).query(" + idFilter.toJson() + ")");
        List<Document> spreadsheetRowDocuments = getSpreadsheetRowDAO(testMode).query(idFilter);
        performanceLogger.done();

        String jsonOut = DAOUtilities.serialize(spreadsheetRowDocuments);
        return jsonOut;
    }


    public SpreadsheetRowDocumentDAO getSpreadsheetRowDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return spreadsheetRowDocumentDAO;
        }
        else {
            return spreadsheetRowDocumentDAO_FOR_TESTING_PURPOSES;
        }
    }
}
