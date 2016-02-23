package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.noCellCollectionApproach;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IFileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToSave;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.noCellCollectionsApproach.nc_DatasetDAO;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.common.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DatasetDocument;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FailedToExtractDataFromFile;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class nc_DatasetBO extends AbsDatasetBO {

    protected static Logger log = Logger.getLogger(nc_DatasetBO.class);

    protected IDatasetReader_AllFileTypes generalFileReader;

    public nc_DatasetBO(DataRepositoryApplication dataRepositoryApplication) {

        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {

        datasetDAO = new nc_DatasetDAO(getSettings());
        generalFileReader = new DatasetReader_AllFileTypes();
    }

    public ObjectId addDataset(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            IStoredFile sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<IStoredFile> attachmentFiles)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave, FailedToExtractDataFromFile {

        File storedFile = getPhysicalFile(sourceDocument.getStorageLocation());
        RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfSubdocumentContainingDataIfApplicable, -1);
        IRowCollection rowCollection = new gov.energy.nrel.dataRepositoryApp.model.common.mongodb.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList<>();
        if (attachmentFiles != null) {
            for (IStoredFile attachmentFile : attachmentFiles) {
                attachments.add(attachmentFile);
            }
        }

        DatasetDocument datasetDocument = new DatasetDocument(
                dataCategory,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                sourceDocument,
                nameOfSubdocumentContainingDataIfApplicable,
                attachments);

        ObjectId objectId = null;
        try {
            objectId = getDatasetDAO().add(datasetDocument, rowCollection);
        }
        catch (CompletelyFailedToPersistDataset e) {
            //  FIXME: I'm not doing anyhign here because this code has been abandoned for now.
            log.error(e, e);
        } catch (PartiallyFailedToPersistDataset e) {
            //  FIXME: I'm not doing anyhign here because this code has been abandoned for now.
            log.error(e, e);
        }

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
            StoredFile sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, IOException, FailedToExtractDataFromFile {

        File storedFile = getPhysicalFile(sourceDocument.getStorageLocation());
        RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfSubdocumentContainingDataIfApplicable, maxNumberOfValuesPerRow);
        IRowCollection rowCollection = new gov.energy.nrel.dataRepositoryApp.model.common.mongodb.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList<>();
        for (StoredFile attachmentFile : attachmentFiles) {
            attachments.add(new StoredFile(attachmentFile.getOriginalFileName(), attachmentFile.getStorageLocation()));
        }

        DatasetDocument datasetDocument = new DatasetDocument(
                dataCategory,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                new StoredFile(sourceDocument.getOriginalFileName(), sourceDocument.getStorageLocation()),
                nameOfSubdocumentContainingDataIfApplicable,
                attachments);

        ObjectId objectId = null;
        try {
            objectId = getDatasetDAO().add(datasetDocument, rowCollection);
        } catch (CompletelyFailedToPersistDataset e) {
            //  FIXME: I'm not doing anyhign here because this code has been abandoned for now.
            log.error(e, e);
        } catch (PartiallyFailedToPersistDataset e) {
            //  FIXME: I'm not doing anyhign here because this code has been abandoned for now.
            log.error(e, e);
        }

        return JSON.serialize(objectId);
    }

    public String getDataset(String datasetId) throws UnknownDataset {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(datasetId);

        String jsonOut = DAOUtilities.serialize(datasetDocument);
        return jsonOut;
    }

    public IDeleteResults removeDatasetFromDatabaseAndMoveItsFiles(String datasetId)
            throws UnknownDataset {

        // This implementation is weak in that it has no atomicity.

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        String storageLocation = datasetDocument.getMetadata().getSourceDocument().getStorageLocation();

        IFileStorageBO fileStorageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();

        try {
            fileStorageBO.deleteFolder(storageLocation);
        } catch (java.io.IOException e) {
            //  FIXME: I'm not doing anyhign here because this code has been abandoned for now.
            log.error(e, e);
        }

        List<IStoredFile> attachments = datasetDocument.getMetadata().getAttachments();
        for (IStoredFile attachment : attachments) {
            try {

                fileStorageBO.deleteFolder(attachment.getStorageLocation());
            } catch (java.io.IOException e) {
                //  FIXME: I'm not doing anyhign here because this code has been abandoned for now.
                log.error(e, e);
            }
        }

        try {
            return datasetDAO.delete(datasetId);
        }
        catch (UnknownEntity e) {
            log.warn("This is odd, as we just retrieved the dataset " + datasetId + ".", e);
            throw new UnknownDataset(datasetId);
        }
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
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, IOException, FailedToSave, FailedToExtractDataFromFile {

        Date timestamp = new Date();

        IFileStorageBO fileStorageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();

        IStoredFile theDataFileThatWasStored = fileStorageBO.saveFile(timestamp, "", sourceDocument);

        List<IStoredFile> theAttachmentsThatWereStored = new ArrayList<>();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {
            theAttachmentsThatWereStored.add(fileStorageBO.saveFile(timestamp, "attachments", attachmentFile));
        }

        ObjectId objectId = null;
        try {
            objectId = addDataset(
                    dataCategory,
                    submissionDate,
                    submitter,
                    projectName,
                    chargeNumber,
                    comments,
                    theDataFileThatWasStored,
                    nameOfSubdocumentContainingDataIfApplicable,
                    theAttachmentsThatWereStored);

            return JSON.serialize(objectId);
        }
        catch (Throwable e) {
            try {
                String fileName = theDataFileThatWasStored.getOriginalFileName();
                String path = fileName.substring(0, fileName.lastIndexOf("/"));
                fileStorageBO.deleteFolder(path);
            } catch (java.io.IOException e1) {
                //  FIXME: I'm not doing anyhign here because this code has been abandoned for now.
                log.error(e, e);
            }

            throw e;
        }
    }
}
