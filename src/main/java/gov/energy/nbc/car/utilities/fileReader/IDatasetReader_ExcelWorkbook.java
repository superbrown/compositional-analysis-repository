package gov.energy.nbc.car.utilities.fileReader;

import gov.energy.nbc.car.utilities.fileReader.dto.RowCollection;
import gov.energy.nbc.car.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;

import java.io.File;
import java.io.IOException;


public interface IDatasetReader_ExcelWorkbook extends IDatasetReader {

    RowCollection extractDataFromFile(File file, String nameOfSubdocumentContainingDataIfApplicable)
            throws IOException, InvalidValueFoundInHeader, UnsupportedFileExtension;
}
