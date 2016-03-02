package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.NotAnExcelWorkbook;

import java.io.File;
import java.io.IOException;


public interface IDatasetReader_ExcelWorkbook extends IDatasetReader {

    RowCollection extractDataFromFile(File file, String nameOfSubdocumentContainingDataIfApplicable)
            throws IOException, FileContainsInvalidColumnName, NotAnExcelWorkbook, UnsanitaryData;
}
