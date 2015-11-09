package gov.energy.nbc.car.businessObject.multipleCellSchemaApproach;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.businessObject.DeletionFailure;
import gov.energy.nbc.car.businessObject.IDatasetBO;
import gov.energy.nbc.car.businessObject.TestMode;
import gov.energy.nbc.car.businessObject.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.IDatasetDAO;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.dao.mongodb.exception.UnableToDeleteFile;
import gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach.m_DatasetDAO;
import gov.energy.nbc.car.fileReader.FileReader;
import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.common.RowCollection;
import gov.energy.nbc.car.model.common.StoredFile;
import gov.energy.nbc.car.model.document.DatasetDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class m_DatasetBO implements IDatasetBO {

    Logger log = Logger.getLogger(this.getClass());

    protected m_DatasetDAO datasetDAO;
    protected m_DatasetDAO datasetDAO_FOR_UNIT_TESTING_PURPOSES;

    protected FileReader fileReader;

    public m_DatasetBO(ISettings settings,
                       ISettings settings_forUnitTestingPurposes) {

        datasetDAO = new m_DatasetDAO(settings);
        datasetDAO_FOR_UNIT_TESTING_PURPOSES = new m_DatasetDAO(settings_forUnitTestingPurposes);

        fileReader = new FileReader();
    }

    @Override
    public String addDataset(
            TestMode testMode,
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nbc.car.businessObject.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.businessObject.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getPhysicalFile(testMode, dataFile.storageLocation);
        RowCollection rowCollection = fileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, -1);

        List<StoredFile> attachments = new ArrayList();
        if (attachmentFiles != null) {
            for (gov.energy.nbc.car.businessObject.dto.StoredFile attachmentFile : attachmentFiles) {
                attachments.add(new StoredFile(attachmentFile.originalFileName, attachmentFile.storageLocation));
            }
        }

        DatasetDocument datasetDocument = new DatasetDocument(
                dataCategory,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                new StoredFile(dataFile.originalFileName, dataFile.storageLocation),
                attachments);

        ObjectId objectId = getDatasetDAO(testMode).add(datasetDocument, rowCollection);

        return objectId.toHexString();
    }

    @Override
    public String addDataset(
            int maxNumberOfValuesPerRow,
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nbc.car.businessObject.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.businessObject.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getPhysicalFile(TestMode.TEST_MODE, dataFile.storageLocation);
        RowCollection rowCollection = fileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, maxNumberOfValuesPerRow);

        List<StoredFile> attachments = new ArrayList();
        for (gov.energy.nbc.car.businessObject.dto.StoredFile attachmentFile : attachmentFiles) {
            attachments.add(new StoredFile(attachmentFile.originalFileName, attachmentFile.storageLocation));
        }

        DatasetDocument datasetDocument = new DatasetDocument(
                dataCategory,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                new StoredFile(dataFile.originalFileName, dataFile.storageLocation),
                attachments);

        ObjectId objectId = getDatasetDAO(TestMode.TEST_MODE).add(datasetDocument, rowCollection);

        return objectId.toHexString();
    }

    protected File getPhysicalFile(TestMode testMode, String storageLocation) {

        return Application.getBusinessObjects().getPhysicalFileBO().getDataFileDAO(testMode).getFile(storageLocation);
    }

    @Override
    public String getDataset(TestMode testMode, String datasetId) {

        DatasetDocument datasetDocument = getDatasetDAO(testMode).getDataset(datasetId);
        if (datasetDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(datasetDocument);
        return jsonOut;
    }

    @Override
    public String getAllDatasets(TestMode testMode) {

        FindIterable<Document> datasets = getDatasetDAO(testMode).getAll();

        String jsonOut = DAOUtilities.serialize(datasets);
        return jsonOut;
    }

    @Override
    public long deleteDataset(TestMode testMode,
                              String datasetId) throws DeletionFailure {

        m_DatasetDAO datasetDAO = getDatasetDAO(testMode);
        DatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);
        DeleteResults deleteResults = datasetDAO.delete(datasetId);

        if (deleteResults.wasAcknowledged() == false) {
            throw new DeletionFailure(deleteResults);
        }

        long numberOfObjectsDeleted = deleteResults.getDeletedCount();

        String storageLocation = datasetDocument.getMetadata().getUploadedFile().getStorageLocation();
        try {
            Application.getBusinessObjects().getPhysicalFileBO().deletFile(storageLocation);
        }
        catch (UnableToDeleteFile e) {
            log.warn(e);
        }

        return numberOfObjectsDeleted;
    }

    @Override
    public String addDataset(
            TestMode testMode,
            String dataCategory,
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
                Application.getBusinessObjects().getPhysicalFileBO().saveFile(testMode, dataFile);

        List<gov.energy.nbc.car.businessObject.dto.StoredFile> theAttachmentsThatWereStored = new ArrayList();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {
            theAttachmentsThatWereStored.add(Application.getBusinessObjects().getPhysicalFileBO().saveFile(testMode, attachmentFile));
        }

        String objectId = addDataset(
                testMode,
                dataCategory,
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

//    public String addDataset(TestMode testMode,
//                                 String jsonIn) {
//
//        Dataset_new_DAO datasetDAO = getDatasetDAO(testMode);
//
//        Dataset dataset = new Dataset(jsonIn);
//        ObjectId objectId = datasetDAO.add(dataset, data);
//
//        return objectId.toHexString();
//    }

    @Override
    public String addDataset(TestMode testMode,
                             String metadataJson,
                             File file,
                             String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        IDatasetDAO datasetDAO = getDatasetDAO(testMode);

        try {
            RowCollection rowCollection = fileReader.extractDataFromDataset(file, nameOfWorksheetContainingTheData);

            Metadata metadata = new Metadata(metadataJson);

            PerformanceLogger performanceLogger = new PerformanceLogger(log, "new Dataset()");
            DatasetDocument datasetDocument = new DatasetDocument(metadata);
            performanceLogger.done();

            ObjectId objectId = datasetDAO.add(datasetDocument, rowCollection);

            return objectId.toHexString();
        }
        catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public m_DatasetDAO getDatasetDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return datasetDAO;
        }
        else {
            return datasetDAO_FOR_UNIT_TESTING_PURPOSES;
        }
    }
}
