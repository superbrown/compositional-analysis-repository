package gov.energy.nbc.car.businessObject;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.businessObject.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.dao.DeleteResults;
import gov.energy.nbc.car.dao.SpreadsheetDocumentDAO;
import gov.energy.nbc.car.dao.UnableToDeleteFile;
import gov.energy.nbc.car.fileReader.FileReader;
import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
import gov.energy.nbc.car.model.common.Data;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.common.StoredFile;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpreadsheetBO {

    Logger log = Logger.getLogger(this.getClass());

    protected SpreadsheetDocumentDAO spreadsheetDocumentDAO;
    protected SpreadsheetDocumentDAO spreadsheetDocumentDAO_FOR_UNIT_TESTING_PURPOSES;

    protected FileReader fileReader;

    public SpreadsheetBO(Settings settings,
                         Settings settings_forUnitTestingPurposes) {

        spreadsheetDocumentDAO = new SpreadsheetDocumentDAO(settings);
        spreadsheetDocumentDAO_FOR_UNIT_TESTING_PURPOSES = new SpreadsheetDocumentDAO(settings_forUnitTestingPurposes);

        fileReader = new FileReader();
    }

    public String addSpreadsheet(
            TestMode testMode,
            String sampleType,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nbc.car.businessObject.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.businessObject.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getDataFile(testMode, dataFile.storageLocation);
        Data data = fileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, -1);

        List<StoredFile> attachments = new ArrayList();
        for (gov.energy.nbc.car.businessObject.dto.StoredFile attachmentFile : attachmentFiles) {
            attachments.add(new StoredFile(attachmentFile.originalFileName, attachmentFile.storageLocation));
        }

        SpreadsheetDocument spreadsheetDocument = new SpreadsheetDocument(
                sampleType,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                new StoredFile(dataFile.originalFileName, dataFile.storageLocation),
                attachments);

        ObjectId objectId = getSpreadsheetDocumentDAO(testMode).add(spreadsheetDocument, data);

        return objectId.toHexString();
    }

    public String addSpreadsheet(
            int maxNumberOfValuesPerRow,
            String sampleType,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nbc.car.businessObject.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.businessObject.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getDataFile(TestMode.TEST_MODE, dataFile.storageLocation);

        Data data = fileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, maxNumberOfValuesPerRow);

        List<StoredFile> attachments = new ArrayList();
        for (gov.energy.nbc.car.businessObject.dto.StoredFile attachmentFile : attachmentFiles) {
            attachments.add(new StoredFile(attachmentFile.originalFileName, attachmentFile.storageLocation));
        }

        SpreadsheetDocument spreadsheetDocument = new SpreadsheetDocument(
                sampleType,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                new StoredFile(dataFile.originalFileName, dataFile.storageLocation),
                attachments);

        ObjectId objectId = getSpreadsheetDocumentDAO(TestMode.TEST_MODE).add(spreadsheetDocument, data);

        return objectId.toHexString();
    }

    protected File getDataFile(TestMode testMode, String storageLocation) {

        return BusinessObjects.dataFileBO.getDataFileDAO(testMode).getFile(storageLocation);
    }

    public File getSpreadsheetDataFile(TestMode testMode, String spreadsheetId) {

        SpreadsheetDocument spreadsheetDocument = getSpreadsheetDocumentDAO(testMode).getSpreadsheet(spreadsheetId);
        String storageLocation = spreadsheetDocument.getMetadata().getUploadedFile().getStorageLocation();

        return BusinessObjects.dataFileBO.getDataFileDAO(testMode).getFile(storageLocation);
    }

    public String getSpreadsheet(TestMode testMode,
                                 String spreadsheetId) {

        SpreadsheetDocument spreadsheetDocument = getSpreadsheetDocumentDAO(testMode).getSpreadsheet(spreadsheetId);
        if (spreadsheetDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(spreadsheetDocument);
        return jsonOut;
    }

    public String getAllSpreadsheets(TestMode testMode) {

        FindIterable<Document> spreadsheetDocuments = getSpreadsheetDocumentDAO(testMode).getAll();

        String jsonOut = DAOUtilities.serialize(spreadsheetDocuments);
        return jsonOut;
    }

    public long deleteSpreadsheet(TestMode testMode,
                                  String spreadsheetId) throws DeletionFailure {

        SpreadsheetDocumentDAO spreadsheetDocumentDAO = getSpreadsheetDocumentDAO(testMode);
        SpreadsheetDocument spreadsheetDocument = spreadsheetDocumentDAO.getSpreadsheet(spreadsheetId);
        DeleteResults deleteResults = spreadsheetDocumentDAO.delete(spreadsheetId);

        if (deleteResults.wasAcknowledged() == false) {
            throw new DeletionFailure(deleteResults);
        }

        long numberOfObjectsDeleted = deleteResults.getDeletedCount();

        String storageLocation = spreadsheetDocument.getMetadata().getUploadedFile().getStorageLocation();
        try {
            BusinessObjects.dataFileBO.deletFile(storageLocation);
        }
        catch (UnableToDeleteFile e) {
            log.warn(e);
        }

        return numberOfObjectsDeleted;
    }

    public String addSpreadsheet(
            TestMode testMode,
            String sampleType,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            FileAsRawBytes dataFile,
            String nameOfSheetContainingData,
            List<FileAsRawBytes> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        gov.energy.nbc.car.businessObject.dto.StoredFile theDataFileThatWasStored =
                BusinessObjects.dataFileBO.saveFile(testMode, dataFile);

        List<gov.energy.nbc.car.businessObject.dto.StoredFile> theAttachmentsThatWereStored = new ArrayList();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {
            theAttachmentsThatWereStored.add(BusinessObjects.dataFileBO.saveFile(testMode, attachmentFile));
        }

        String objectId = addSpreadsheet(
                testMode,
                sampleType,
                submissionDate,
                submitter,
                projectName,
                chargeNumber,
                comments,
                theDataFileThatWasStored,
                nameOfSheetContainingData,
                theAttachmentsThatWereStored);

        return objectId;
    }

//    public String addSpreadsheet(TestMode testMode,
//                                 String jsonIn) {
//
//        SpreadsheetDocumentDAO spreadsheetDocumentDAO = getSpreadsheetDocumentDAO(testMode);
//
//        SpreadsheetDocument spreadsheetDocument = new SpreadsheetDocument(jsonIn);
//        ObjectId objectId = spreadsheetDocumentDAO.add(spreadsheetDocument, data);
//
//        return objectId.toHexString();
//    }

    public String addSpreadsheet(TestMode testMode,
                                 String metadataJson,
                                 File file,
                                 String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        SpreadsheetDocumentDAO spreadsheetDocumentDAO = getSpreadsheetDocumentDAO(testMode);

        try {
            Data data = fileReader.extractDataFromSpreadsheet(file, nameOfWorksheetContainingTheData);

            Metadata metadata = new Metadata(metadataJson);

            PerformanceLogger performanceLogger = new PerformanceLogger("new SpreadsheetDocument()");
            SpreadsheetDocument spreadsheetDocument = new SpreadsheetDocument(metadata);
            performanceLogger.done();

            ObjectId objectId = spreadsheetDocumentDAO.add(spreadsheetDocument, data);

            return objectId.toHexString();
        }
        catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public SpreadsheetDocumentDAO getSpreadsheetDocumentDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return spreadsheetDocumentDAO;
        }
        else {
            return spreadsheetDocumentDAO_FOR_UNIT_TESTING_PURPOSES;
        }
    }
}
