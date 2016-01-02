package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.bo.exception.DeletionFailure;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.bson.types.ObjectId;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public interface IDatasetBO {

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
            throws UnsupportedFileExtension, InvalidValueFoundInHeader;

    String getDataset(String datasetId);

    String getAllDatasets();

    long removeDataset(String datasetId) throws DeletionFailure, UnknownDataset;

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
            throws UnsupportedFileExtension, InvalidValueFoundInHeader;

    String addDataset(String metadataJson,
                      File file,
                      String nameOfSubdocumentContainingDataIfApplicable)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader;

    IDatasetDAO getDatasetDAO();

    File getSourceDocument(String datasetId);

    ByteArrayInputStream packageAttachmentsInAZipFile(String datasetId) throws IOException;
}
