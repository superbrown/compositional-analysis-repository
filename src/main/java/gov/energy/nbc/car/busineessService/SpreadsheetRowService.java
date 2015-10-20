package gov.energy.nbc.car.busineessService;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.dao.SpreadsheetRowDocumentDAO;
import gov.energy.nbc.car.model.document.SpreadsheetRowDocument;

import java.util.List;

public class SpreadsheetRowService {

    protected SpreadsheetRowDocumentDAO spreadsheetRowDocumentDAO;
    protected SpreadsheetRowDocumentDAO spreadsheetRowDocumentDAO_FOR_TESTING_PURPOSES;

    public SpreadsheetRowService(Settings settings, Settings settings_forTheTestingPurposes) {

        spreadsheetRowDocumentDAO = new SpreadsheetRowDocumentDAO(settings);
        spreadsheetRowDocumentDAO_FOR_TESTING_PURPOSES = new SpreadsheetRowDocumentDAO(settings_forTheTestingPurposes);
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

    public SpreadsheetRowDocumentDAO getSpreadsheetRowDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return spreadsheetRowDocumentDAO;
        }
        else {
            return spreadsheetRowDocumentDAO_FOR_TESTING_PURPOSES;
        }
    }
}
