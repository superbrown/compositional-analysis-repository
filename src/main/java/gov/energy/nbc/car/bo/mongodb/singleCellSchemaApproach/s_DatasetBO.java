package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import com.mongodb.util.JSON;
import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.IPhysicalFileBO;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.model.IRowCollection;
import gov.energy.nbc.car.model.IStoredFile;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.common.StoredFile;
import gov.energy.nbc.car.model.mongodb.document.DatasetDocument;
import gov.energy.nbc.car.settings.ISettings;
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

public class s_DatasetBO extends gov.energy.nbc.car.bo.mongodb.AbsDatasetBO implements IDatasetBO {

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
            gov.energy.nbc.car.dao.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.dao.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getPhysicalFile(dataFile.storageLocation);
        RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, -1);
        IRowCollection rowCollection = new gov.energy.nbc.car.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList();
        if (attachmentFiles != null) {
            for (gov.energy.nbc.car.dao.dto.StoredFile attachmentFile : attachmentFiles) {
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
                nameOfWorksheetContainingTheData,
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
            gov.energy.nbc.car.dao.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<gov.energy.nbc.car.dao.dto.StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        File storedFile = getPhysicalFile(dataFile.storageLocation);
        RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfWorksheetContainingTheData, maxNumberOfValuesPerRow);
        IRowCollection rowCollection = new gov.energy.nbc.car.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList();
        for (gov.energy.nbc.car.dao.dto.StoredFile attachmentFile : attachmentFiles) {
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
                nameOfWorksheetContainingTheData,
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

    public long removeDataset(String datasetId) throws DeletionFailure {

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        String storageLocation = datasetDocument.getMetadata().getUploadedFile().getStorageLocation();
        try {
            physicalFileBO.moveFilesToRemovedFilesLocation(storageLocation);
        }
        catch (IOException e) {
            log.warn(e);
            throw new RuntimeException(e);
        }

        List<IStoredFile> attachments = datasetDocument.getMetadata().getAttachments();
        for (IStoredFile attachment : attachments) {
            try {

                physicalFileBO.moveFilesToRemovedFilesLocation(attachment.getStorageLocation());
            }
            catch (IOException e) {
                log.warn(e);
            }
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
            FileAsRawBytes dataFile,
            String nameOfSheetContainingData,
            List<FileAsRawBytes> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        Date timestamp = new Date();

        gov.energy.nbc.car.dao.dto.StoredFile theDataFileThatWasStored = physicalFileBO.saveFile(timestamp, dataFile);

        List<gov.energy.nbc.car.dao.dto.StoredFile> theAttachmentsThatWereStored = new ArrayList();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {
            theAttachmentsThatWereStored.add(physicalFileBO.saveFile(timestamp, attachmentFile));
        }

        ObjectId objectId = addDataset(
                dataCategory,
                submissionDate,
                submitter,
                projectName,
                chargeNumber,
                comments,
                theDataFileThatWasStored,
                nameOfSheetContainingData,
                theAttachmentsThatWereStored);

        IDatasetDocument persistedDatasetDocument = getDatasetDAO().getDataset(objectId.toHexString());

        FileAsRawBytes datasetDocumentAsRawBytes =
                new FileAsRawBytes("DATASET_METADATA.json", JSON.serialize(persistedDatasetDocument).getBytes());

        physicalFileBO.saveFile(timestamp, datasetDocumentAsRawBytes);

        return JSON.serialize(objectId);
    }

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
