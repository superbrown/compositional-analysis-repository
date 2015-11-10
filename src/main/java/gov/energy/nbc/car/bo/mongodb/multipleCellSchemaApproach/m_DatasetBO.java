package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.TestMode;
import gov.energy.nbc.car.bo.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.dto.DeleteResults;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach.m_DatasetDAO;
import gov.energy.nbc.car.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nbc.car.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.exception.UnsupportedFileExtension;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.model.IRowCollection;
import gov.energy.nbc.car.model.IStoredFile;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.common.StoredFile;
import gov.energy.nbc.car.model.mongodb.document.DatasetDocument;
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

    protected IDatasetReader_AllFileTypes generalFileReader;

    public m_DatasetBO(ISettings settings,
                       ISettings settings_forUnitTestingPurposes) {

        datasetDAO = new m_DatasetDAO(settings);
        datasetDAO_FOR_UNIT_TESTING_PURPOSES = new m_DatasetDAO(settings_forUnitTestingPurposes);

        generalFileReader = new DatasetReader_AllFileTypes();
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
            gov.energy.nbc.car.bo.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.bo.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getPhysicalFile(testMode, dataFile.storageLocation);
        IRowCollection rowCollection = generalFileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, -1);

        List<IStoredFile> attachments = new ArrayList();
        if (attachmentFiles != null) {
            for (gov.energy.nbc.car.bo.dto.StoredFile attachmentFile : attachmentFiles) {
                attachments.add(new StoredFile(attachmentFile.originalFileName, attachmentFile.storageLocation));
            }
        }

        IDatasetDocument datasetDocument = new DatasetDocument(
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

        gov.energy.nbc.car.bo.dto.StoredFile theDataFileThatWasStored =
                Application.getBusinessObjects().getPhysicalFileBO().saveFile(testMode, dataFile);

        List<gov.energy.nbc.car.bo.dto.StoredFile> theAttachmentsThatWereStored = new ArrayList();

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
            IRowCollection rowCollection = generalFileReader.extractDataFromDataset(file, nameOfWorksheetContainingTheData);

            IMetadata metadata = new Metadata(metadataJson);

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
