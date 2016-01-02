package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;

import java.io.File;
import java.io.IOException;

public interface IDatasetReader_AllFileTypes extends IDatasetReader {

    RowCollection extractDataFromFile(File file,
                                      String nameOfSubdocumentContainingDataIfApplicable,
                                      int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader;

    RowCollection extractDataFromDataset(File file,
                                         String nameOfSubdocumentContainingDataIfApplicable)
            throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader;

    boolean isAnExcelFile(String filename);

    boolean canReadFile(File file);
}
