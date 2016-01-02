package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;

import java.io.File;
import java.io.IOException;


public interface IDatasetReader_ExcelWorkbook extends IDatasetReader {

    RowCollection extractDataFromFile(File file, String nameOfSubdocumentContainingDataIfApplicable)
            throws IOException, InvalidValueFoundInHeader, UnsupportedFileExtension;
}
