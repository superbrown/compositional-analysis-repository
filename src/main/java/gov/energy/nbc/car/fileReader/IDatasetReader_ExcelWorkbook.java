package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.fileReader.dto.RowCollection;
import gov.energy.nbc.car.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.exception.UnsupportedFileExtension;

import java.io.File;
import java.io.IOException;


public interface IDatasetReader_ExcelWorkbook extends IDatasetReader {

    RowCollection extractDataFromFile(File file, String nameOfWorksheetContainingTheData)
            throws IOException, InvalidValueFoundInHeader, UnsupportedFileExtension;
}
