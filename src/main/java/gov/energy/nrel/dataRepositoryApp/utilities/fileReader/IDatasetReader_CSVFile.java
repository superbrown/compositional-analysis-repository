package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;

import java.io.File;
import java.io.IOException;


public interface IDatasetReader_CSVFile extends IDatasetReader {

    RowCollection extractDataFromFile(File file, int maxNumberOfValuesPerRow)
            throws IOException, FileContainsInvalidColumnName, UnsupportedFileExtension, UnsanitaryData;
}
