package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellCollectionApproach;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IFileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToDeleteFiles;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToSave;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBO;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DatasetTransactionTokenDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach.sc_DatasetDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DatasetDocument;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class sc_DatasetBO extends AbsDatasetBO implements IDatasetBO {

    protected static Logger log = Logger.getLogger(sc_DatasetBO.class);

    protected IDatasetReader_AllFileTypes generalFileReader;

    public sc_DatasetBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {

        datasetDAO = new sc_DatasetDAO(getSettings());
        generalFileReader = new DatasetReader_AllFileTypes();
        datasetTransactionTokenDAO = new DatasetTransactionTokenDAO(getSettings());
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
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave {

        File storedFile = getPhysicalFile(sourceDocument.getStorageLocation());
        RowCollection dataUpload = null;
        try {
            dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfSubdocumentContainingDataIfApplicable, -1);
        }
        catch (UnsupportedFileExtension e) {
            throw e;
        }
        catch (FileContainsInvalidColumnName e) {
            throw e;
        }
        IRowCollection rowCollection = new gov.energy.nrel.dataRepositoryApp.model.common.mongodb.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList();
        if (attachmentFiles != null) {
            for (IStoredFile attachmentFile : attachmentFiles) {
                attachments.add(new StoredFile(attachmentFile.getOriginalFileName(), attachmentFile.getStorageLocation()));
            }
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

        ObjectId datasetObjectId = null;
        try {

            datasetObjectId = getDatasetDAO().add(datasetDocument, rowCollection);
        }
        catch (PartiallyFailedToPersistDataset e) {

            datasetObjectId = e.getDatasetObjectId();

            try {
                removeDatasetFromDatabaseAndDeleteItsFiles(datasetObjectId);
                // This needs to be called here because the the calling code may not have been able to call it since an
                // exception was thrown.
                removeDatasetTransactionToken(datasetObjectId);
                throw new FailedToSave(e);
            }
            catch (UnknownDataset e1) {

                log.warn(e1);
                try {
                    deleteFiles(datasetDocument);
                    removeDatasetTransactionToken(datasetObjectId);
                }
                catch (FailedToDeleteFiles e2) {
                    log.warn(e1);
                }

                throw new FailedToSave(e);
            }
            catch (FailedToDeleteFiles e1) {
                log.warn(e1);
                throw new FailedToSave(e);
            }
        }
        catch (CompletelyFailedToPersistDataset e) {

            try {
                deleteFiles(datasetDocument);
            }
            catch (FailedToDeleteFiles e1) {
                log.warn(e1);
            }

            throw new FailedToSave(e);
        }

        return datasetObjectId;
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
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave {

        Date timestamp = new Date();

        IFileStorageBO fileStorageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();

        IStoredFile theDataFileThatWasStored = fileStorageBO.saveFile(timestamp, "", sourceDocument);

        List<IStoredFile> theAttachmentsThatWereStored = new ArrayList();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {

            IStoredFile attachments = fileStorageBO.saveFile(timestamp, "attachments", attachmentFile);
            theAttachmentsThatWereStored.add(attachments);
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


}
