package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellCollectionsApproach;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IFileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToSave;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach.mc_DatasetDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DatasetDocument;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer.IValueSanitizer;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.UnsanitaryData;
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

public class mc_DatasetBO extends AbsDatasetBO {

    protected static Logger log = Logger.getLogger(mc_DatasetBO.class);

    protected IDatasetReader_AllFileTypes generalFileReader;

    public mc_DatasetBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {

        super.init();

        datasetDAO = new mc_DatasetDAO(getSettings());

        IValueSanitizer valueSanitizer = this.getDataRepositoryApplication().getValueSanitizer();
        generalFileReader = new DatasetReader_AllFileTypes(valueSanitizer);
    }

    @Override
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
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave, FailedToExtractDataFromFile, UnsanitaryData {

        File storedFile = getPhysicalFile(sourceDocument.getStorageLocation());
        RowCollection dataUpload = generalFileReader.extractDataFromFile(storedFile, nameOfSubdocumentContainingDataIfApplicable, -1);
        IRowCollection rowCollection = new gov.energy.nrel.dataRepositoryApp.model.common.mongodb.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        List<IStoredFile> attachments = new ArrayList<>();
        if (attachmentFiles != null) {
            for (IStoredFile attachmentFile : attachmentFiles) {
                attachments.add(attachmentFile);
            }
        }

        IDatasetDocument datasetDocument = new DatasetDocument(
                dataCategory,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                sourceDocument,
                nameOfSubdocumentContainingDataIfApplicable,
                attachments);

        ObjectId objectId = getDatasetDAO().add(datasetDocument, rowCollection);

        return objectId;
    }

    @Override
    public String getDataset(String datasetId) throws UnknownDataset {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(datasetId);
        if (datasetDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(datasetDocument);
        return jsonOut;
    }

    @Override
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
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, IOException, FailedToExtractDataFromFile, FailedToSave, UnsanitaryData {

        Date timestamp = new Date();

        IFileStorageBO fileSotrageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();

        IStoredFile theDataFileThatWasStored = fileSotrageBO.saveFile(timestamp, "", sourceDocument);

        List<IStoredFile> theAttachmentsThatWereStored = new ArrayList<>();

        for (FileAsRawBytes attachmentFile : attachmentFiles) {
            theAttachmentsThatWereStored.add(fileSotrageBO.saveFile(timestamp, "attachments", attachmentFile));
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

        return JSON.serialize(objectId);
    }

    @Override
    public mc_DatasetDAO getDatasetDAO() {

        return (mc_DatasetDAO) datasetDAO;
    }
}
