package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.PhysicalFileBO;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.multipleCellCollectionsApproach.m_DatasetDAO;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.model.IRowCollection;
import gov.energy.nbc.car.model.IStoredFile;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.common.StoredFile;
import gov.energy.nbc.car.model.mongodb.document.DatasetDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.dto.RowCollection;
import gov.energy.nbc.car.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;
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
    protected PhysicalFileBO physicalFileBO;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public m_DatasetBO(ISettings settings) {

        datasetDAO = new m_DatasetDAO(settings);
        physicalFileBO = new PhysicalFileBO(settings);

        generalFileReader = new DatasetReader_AllFileTypes();
    }

    @Override
    public String addDataset(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nbc.car.dao.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.dao.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = physicalFileBO.getFile(dataFile.storageLocation);
        RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, -1);
        IRowCollection rowCollection = new gov.energy.nbc.car.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList();
        if (attachmentFiles != null) {
            for (gov.energy.nbc.car.dao.dto.StoredFile attachmentFile : attachmentFiles) {
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

        ObjectId objectId = getDatasetDAO().add(datasetDocument, rowCollection);

        return objectId.toHexString();
    }

    @Override
    public String getDataset(String datasetId) {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(datasetId);
        if (datasetDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(datasetDocument);
        return jsonOut;
    }

    @Override
    public String getAllDatasets() {

        Iterable<Document> datasets = getDatasetDAO().getAll();

        String jsonOut = DAOUtilities.serialize(datasets);
        return jsonOut;
    }

    @Override
    public long deleteDataset(String datasetId) throws DeletionFailure {

        m_DatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        String storageLocation = datasetDocument.getMetadata().getUploadedFile().getStorageLocation();
        try {
            physicalFileBO.deletFile(storageLocation);
        }
        catch (UnableToDeleteFile e) {
            log.warn(e);
        }

        List<IStoredFile> attachments = datasetDocument.getMetadata().getAttachments();
        for (IStoredFile attachment : attachments) {
            try {

                physicalFileBO.deletFile(attachment.getStorageLocation());
            }
            catch (UnableToDeleteFile e) {
                log.warn(e);
            }
        }

        IDeleteResults deleteResults = datasetDAO.delete(datasetId);

        return deleteResults.getDeletedCount();
    }

    @Override
    public String addDataset(
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

        gov.energy.nbc.car.dao.dto.StoredFile theDataFileThatWasStored = physicalFileBO.saveFile(dataFile);

        List<gov.energy.nbc.car.dao.dto.StoredFile> theAttachmentsThatWereStored = new ArrayList();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {
            theAttachmentsThatWereStored.add(physicalFileBO.saveFile(attachmentFile));
        }

        String objectId = addDataset(
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
    public String addDataset(String metadataJson,
                             File file,
                             String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        IDatasetDAO datasetDAO = getDatasetDAO();

        try {
            RowCollection dataUpload = generalFileReader.extractDataFromDataset(file, nameOfWorksheetContainingTheData);
            IRowCollection rowCollection = new gov.energy.nbc.car.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

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
    public m_DatasetDAO getDatasetDAO() {

        return datasetDAO;
    }
}
