package gov.energy.nbc.car.businessObject;

import gov.energy.nbc.car.businessObject.dto.FileAsRawBytes;
import gov.energy.nbc.car.businessObject.dto.StoredFile;
import gov.energy.nbc.car.dao.mongodb.IDatasetDAO;
import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by mbrown on 11/8/2015.
 */
public interface IDatasetBO {
    String addDataset(
            TestMode testMode,
            String dataCategory,
            Date submissionDate,
            String submitter,
            String projectName,
            String chargeNumber,
            String comments,
            gov.energy.nbc.car.businessObject.dto.StoredFile dataFile,
            String nameOfWorksheetContainingTheData,
            List<StoredFile> attachmentFiles)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader;

        String addDataset(
                int maxNumberOfValuesPerRow,
                String dataCategory,
                Date submissionDate,
                String submitter,
                String projectName,
                String chargeNumber,
                String comments,
                StoredFile dataFile,
                String nameOfWorksheetContainingTheData,
                List<StoredFile> attachmentFiles)
                    throws UnsupportedFileExtension, InvalidValueFoundInHeader;

        String getDataset(TestMode testMode, String datasetId);

        String getAllDatasets(TestMode testMode);

        long deleteDataset(TestMode testMode,
                           String datasetId) throws DeletionFailure;

        String addDataset(
                TestMode testMode,
                String dataCategory,
                Date submissionDate,
                String submitter,
                String projectName,
                String chargeNumber,
                String comments,
                FileAsRawBytes dataFile,
                String nameOfSheetContainingData,
                List<FileAsRawBytes> attachmentFiles)
                                                          throws UnsupportedFileExtension, InvalidValueFoundInHeader;

        String addDataset(TestMode testMode,
                          String metadataJson,
                          File file,
                          String nameOfWorksheetContainingTheData)
                                                                  throws UnsupportedFileExtension, InvalidValueFoundInHeader;

        IDatasetDAO getDatasetDAO(TestMode testMode);
}
