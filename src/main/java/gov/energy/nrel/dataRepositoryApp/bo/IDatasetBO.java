package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.bo.exception.ArchiveFailure;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToSave;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
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

    ObjectId addDataset(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            StoredFile sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, PartiallyFailedToPersistDataset, FailedToSave, UnknownDataset;

    String getDataset(String datasetId) throws UnknownDataset;

    IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(ObjectId datasetId)
            throws ArchiveFailure, UnknownDataset;

    IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(String datasetId)
                    throws UnknownDataset;

    IDeleteResults removeDatasetFromTheDatabase(String datasetId)
                            throws UnknownDataset;

    void deleteFiles(IDatasetDocument datasetDocument);

    String getAllDatasets();

    IDeleteResults removeDatasetFromDatabaseAndMoveItsFiles(ObjectId datasetId)
            throws ArchiveFailure, UnknownDataset;

    IDeleteResults removeDatasetFromDatabaseAndMoveItsFiles(String datasetId)
            throws UnknownDataset;

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
}
