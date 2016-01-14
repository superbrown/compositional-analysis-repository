package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToDeleteFiles;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToSave;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.model.common.IMetadata;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.bson.types.ObjectId;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public interface IDatasetBO extends IBO {

    String getDataset(String datasetId) throws UnknownDataset;

    IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(ObjectId datasetId)
            throws UnknownDataset, FailedToDeleteFiles;

    IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(String datasetId)
            throws UnknownDataset, FailedToDeleteFiles;

    void deleteFiles(IDatasetDocument datasetDocument) throws IOException, FailedToDeleteFiles;

    String getAllDatasets();

    IDeleteResults removeDatasetFromDatabaseAndMoveItsFiles(String datasetId)
            throws UnknownDataset, FailedToDeleteFiles;

    void addDatasetTransactionToken(ObjectId datasetObjectId);

    void removeDatasetTransactionToken(ObjectId datasetObjectId);

    List<ObjectId> getDatasetIdsForAllIncompleteDatasetUploadCleanups();

    ObjectId addDataset(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            IStoredFile sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<IStoredFile> attachmentFiles)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave;

    ObjectId addDataset(IMetadata metadata)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave;

    String addDataset(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            FileAsRawBytes sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<FileAsRawBytes> attachmentFiles)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToSave;

    IDatasetDAO getDatasetDAO();

    File getSourceDocument(String datasetId);

    ByteArrayInputStream packageAttachmentsInAZipFile(String datasetId) throws IOException;

    List<String> attemptToCleanupDataFromAllPreviouslyIncompleteDatasetUploads();
}
