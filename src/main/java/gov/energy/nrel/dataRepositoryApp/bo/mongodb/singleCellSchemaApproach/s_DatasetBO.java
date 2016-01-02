package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IPhysicalFileBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DeletionFailure;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IMetadata;
import gov.energy.nrel.dataRepositoryApp.model.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.DatasetDocument;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.utilities.PerformanceLogger;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBO;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class s_DatasetBO extends AbsDatasetBO implements IDatasetBO {

    Logger log = Logger.getLogger(this.getClass());

    protected IDatasetReader_AllFileTypes generalFileReader;

    public s_DatasetBO(ISettings settings) {
        super(settings);

        generalFileReader = new DatasetReader_AllFileTypes();
    }

    public ObjectId addDataset(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getPhysicalFile(sourceDocument.storageLocation);
        gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfSubdocumentContainingDataIfApplicable, -1);
        IRowCollection rowCollection = new gov.energy.nrel.dataRepositoryApp.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList();
        if (attachmentFiles != null) {
            for (gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile attachmentFile : attachmentFiles) {
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
                new StoredFile(sourceDocument.originalFileName, sourceDocument.storageLocation),
                nameOfSubdocumentContainingDataIfApplicable,
                attachments);

        ObjectId objectId = getDatasetDAO().add(datasetDocument, rowCollection);

        return objectId;
    }

    public String addDataset(
            int maxNumberOfValuesPerRow,
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getPhysicalFile(sourceDocument.storageLocation);
        gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfSubdocumentContainingDataIfApplicable, maxNumberOfValuesPerRow);
        IRowCollection rowCollection = new gov.energy.nrel.dataRepositoryApp.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList();
        for (gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile attachmentFile : attachmentFiles) {
            attachments.add(new StoredFile(attachmentFile.originalFileName, attachmentFile.storageLocation));
        }

        DatasetDocument datasetDocument = new DatasetDocument(
                dataCategory,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                new StoredFile(sourceDocument.originalFileName, sourceDocument.storageLocation),
                nameOfSubdocumentContainingDataIfApplicable,
                attachments);

        ObjectId objectId = getDatasetDAO().add(datasetDocument, rowCollection);

        return JSON.serialize(objectId);
    }

    protected File getPhysicalFile(String storageLocation) {

        return getPhysicalFileBO().getFileStorageDAO().getFile(storageLocation);
    }

    public String getDataset(String datasetId) {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(datasetId);
        if (datasetDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(datasetDocument);
        return jsonOut;
    }

    public String getAllDatasets() {

        Iterable<Document> datasets = getDatasetDAO().getAll();

        String jsonOut = DAOUtilities.serialize(datasets);
        return jsonOut;
    }

    public long removeDataset(String datasetId) throws DeletionFailure, UnknownDataset {

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        if (datasetDocument == null) {
            throw new UnknownDataset(datasetId);
        }

        String storageLocation = datasetDocument.getMetadata().getSourceDocument().getStorageLocation();
        try {
            physicalFileBO.moveFilesToRemovedFilesLocation(storageLocation);
        }
        catch (IOException e) {
            log.warn(e);
            throw new RuntimeException(e);
        }

        datasetDAO.delete(datasetId);
        return 0;
    }

    public String addDataset(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            FileAsRawBytes sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<FileAsRawBytes> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        Date timestamp = new Date();

        gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile theDataFileThatWasStored = physicalFileBO.saveFile(timestamp, "", sourceDocument);

        List<gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile> theAttachmentsThatWereStored = new ArrayList();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {
            theAttachmentsThatWereStored.add(physicalFileBO.saveFile(timestamp, "attachments", attachmentFile));
        }

        ObjectId objectId = addDataset(
                dataCategory,
                submissionDate,
                submitter,
                projectName,
                chargeNumber,
                comments,
                theDataFileThatWasStored,
                nameOfSubdocumentContainingDataIfApplicable,
                theAttachmentsThatWereStored);

        saveMetadataAsJsonFile(timestamp, objectId);

        return JSON.serialize(objectId);
    }

    private void saveMetadataAsJsonFile(Date timestamp, ObjectId objectId) {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(objectId.toHexString());
        byte[] json = JSON.serialize(datasetDocument).getBytes();

        FileAsRawBytes jsonAsRawBytes = new FileAsRawBytes("DATASET_METADATA.json", json);

        physicalFileBO.saveFile(timestamp, "", jsonAsRawBytes);
    }

    public String addDataset(String metadataJson,
                             File file,
                             String nameOfSubdocumentContainingDataIfApplicable)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        IDatasetDAO datasetDAO = getDatasetDAO();

        try {
            gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection dataUpload = generalFileReader.extractDataFromDataset(file, nameOfSubdocumentContainingDataIfApplicable);
            IRowCollection rowCollection = new gov.energy.nrel.dataRepositoryApp.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

            IMetadata metadata = new Metadata(metadataJson);

            PerformanceLogger performanceLogger = new PerformanceLogger(log, "new Dataset()");
            DatasetDocument datasetDocument = new DatasetDocument(metadata);
            performanceLogger.done();

            ObjectId objectId = datasetDAO.add(datasetDocument, rowCollection);

            return JSON.serialize(objectId);
        }
        catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public IPhysicalFileBO getPhysicalFileBO() {
        return physicalFileBO;
    }
}
