package gov.energy.nbc.car.businessService;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.dao.DeleteResults;
import gov.energy.nbc.car.dao.SpreadsheetDocumentDAO;
import gov.energy.nbc.car.fileReader.NonStringValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
import gov.energy.nbc.car.model.common.Data;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;

public class SpreadsheetService {

    Logger log = Logger.getLogger(this.getClass());

    protected SpreadsheetDocumentDAO spreadsheetDocumentDAO;
    protected SpreadsheetDocumentDAO spreadsheetDocumentDAO_FOR_UNIT_TESTING_PURPOSES;

    protected BusinessServiceUtilities businessServiceUtilities;

    public SpreadsheetService(Settings settings,
                              Settings settings_forUnitTestingPurposes) {

        spreadsheetDocumentDAO = new SpreadsheetDocumentDAO(settings);
        spreadsheetDocumentDAO_FOR_UNIT_TESTING_PURPOSES = new SpreadsheetDocumentDAO(settings_forUnitTestingPurposes);

        businessServiceUtilities = new BusinessServiceUtilities();
    }

    public String getSpreadsheet(TestMode testMode,
                                 String spreadsheetId) {

        SpreadsheetDocument spreadsheetDocument = getSpreadsheetDocument(testMode, spreadsheetId);
        if (spreadsheetDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(spreadsheetDocument);
        return jsonOut;
    }

    public String getSpreadsheetMetadata(TestMode testMode,
                                         String spreadsheetId) {

        Metadata metadata = getSpreadsheetDAO(testMode).getSpreadsheetMetadata(spreadsheetId);
        if (metadata == null) { return null; }

        String jsonOut = DAOUtilities.serialize(metadata);
        return jsonOut;
    }


    public String getSpreadsheetData(TestMode testMode,
                                     String spreadsheetId) {

        Data data = getSpreadsheetDAO(testMode).getSpreadsheetData(spreadsheetId);
        if (data == null) { return null; }

        String jsonOut = DAOUtilities.serialize(data);
        return jsonOut;
    }

    public String getAllSpreadsheets(TestMode testMode) {

        FindIterable<Document> spreadsheetDocuments = getSpreadsheetDAO(testMode).getAll();

        String jsonOut = DAOUtilities.serialize(spreadsheetDocuments);
        return jsonOut;
    }

    public long deleteSpreadsheet(TestMode testMode,
                                  String spreadsheetId) throws DeletionFailure {

        DeleteResults deleteResults = getSpreadsheetDAO(testMode).delete(spreadsheetId);

        if (deleteResults.wasAcknowledged() == false) {
            throw new DeletionFailure(deleteResults);
        }

        long numberOfObjectsDeleted = deleteResults.getDeletedCount();
        return numberOfObjectsDeleted;
    }

    public String addSpreadsheet(TestMode testMode,
                                 String jsonIn) {

        SpreadsheetDocumentDAO spreadsheetDocumentDAO = getSpreadsheetDAO(testMode);

        SpreadsheetDocument spreadsheetDocument = new SpreadsheetDocument(jsonIn);
        ObjectId objectId = spreadsheetDocumentDAO.add(spreadsheetDocument);

        return objectId.toHexString();
    }

    public String addSpreadsheet(TestMode testMode,
                                 String metadataJson,
                                 File file,
                                 String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, NonStringValueFoundInHeader {

        SpreadsheetDocumentDAO spreadsheetDocumentDAO = getSpreadsheetDAO(testMode);

        try {
            Data data = businessServiceUtilities.extractDataFromSpreadsheet(file, nameOfWorksheetContainingTheData);

            Metadata metadata = new Metadata(metadataJson);

            SpreadsheetDocument spreadsheetDocument = new SpreadsheetDocument(metadata, data);

            ObjectId objectId = spreadsheetDocumentDAO.add(spreadsheetDocument);

            return objectId.toHexString();
        }
        catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public SpreadsheetDocument getSpreadsheetDocument(TestMode testMode,
                                                      String spreadsheetId) {

        SpreadsheetDocument document = getSpreadsheetDAO(testMode).get(spreadsheetId);
        return document;
    }

    public SpreadsheetDocumentDAO getSpreadsheetDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return spreadsheetDocumentDAO;
        }
        else {
            return spreadsheetDocumentDAO_FOR_UNIT_TESTING_PURPOSES;
        }
    }
}
