package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IPhysicalFileBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToSave;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach.s_DatasetDAO;
import gov.energy.nrel.dataRepositoryApp.model.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.DatasetDocument;
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

public class s_DatasetBO extends gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBO implements IDatasetBO {

    Logger log = Logger.getLogger(this.getClass());

    protected IDatasetReader_AllFileTypes generalFileReader;

    public s_DatasetBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {

        datasetDAO = new s_DatasetDAO(getSettings());
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
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave {

        // create "in work" token
        File storedFile = getPhysicalFile(sourceDocument.storageLocation);
        RowCollection dataUpload = null;
        try {
            dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfSubdocumentContainingDataIfApplicable, -1);
        }
        catch (UnsupportedFileExtension e) {
            // remove "in work" token
            throw e;
        }
        catch (FileContainsInvalidColumnName e) {
            // remove "in work" token
            throw e;
        }
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

        ObjectId objectId = null;
        try {

            objectId = getDatasetDAO().add(datasetDocument, rowCollection);
        }
        catch (PartiallyFailedToPersistDataset e) {

            try {
                removeDatasetFromDatabaseAndDeleteItsFiles(e.getDatasetObjectId());
                // remove "in work" token
                throw new FailedToSave(e);
            }
            catch (UnknownDataset e1) {
                log.warn(e1);
                deleteFiles(datasetDocument);
                // remove "in work" token
                throw new FailedToSave(e);
            }
        }
        catch (CompletelyFailedToPersistDataset e) {

            deleteFiles(datasetDocument);
            // remove "in work" token
            throw new FailedToSave(e);
        }

        // remove "in work" token
        return objectId;
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

        IPhysicalFileBO physicalFileBO = getDataRepositoryApplication().getBusinessObjects().getPhysicalFileBO();

        gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile theDataFileThatWasStored =
                physicalFileBO.saveFile(timestamp, "", sourceDocument);

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
}
