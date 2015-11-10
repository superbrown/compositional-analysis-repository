package gov.energy.nbc.car.utilities.fileReader;

import gov.energy.nbc.car.utilities.fileReader.dto.RowCollection;
import gov.energy.nbc.car.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;

import java.io.File;
import java.io.IOException;

public interface IDatasetReader_AllFileTypes extends IDatasetReader {

    RowCollection extractDataFromFile(File file, String nameOfWorksheetContainingTheData, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader;

    RowCollection extractDataFromDataset(File file, String nameOfWorksheetContainingTheData)
                    throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader;

    boolean isAnExcelFile(String filename);

    boolean canReadFile(File file);
}
