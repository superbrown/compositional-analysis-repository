package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FailedToExtractDataFromFile;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;

import java.io.File;

public interface IDatasetReader_AllFileTypes extends IDatasetReader {

    RowCollection extractDataFromFile(File file,
                                      String nameOfSubdocumentContainingDataIfApplicable,
                                      int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, FileContainsInvalidColumnName, FailedToExtractDataFromFile, UnsanitaryData;

    boolean isAnExcelFile(String filename);
}
