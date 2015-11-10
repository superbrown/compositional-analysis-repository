package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.exception.UnsupportedFileExtension;
import gov.energy.nbc.car.model.IRowCollection;

import java.io.File;
import java.io.IOException;

public interface IDatasetReader_AllFileTypes extends IDatasetReader {

    IRowCollection extractDataFromFile(File file, String nameOfWorksheetContainingTheData, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader;

    IRowCollection extractDataFromDataset(File file, String nameOfWorksheetContainingTheData)
                    throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader;

    boolean isAnExcelFile(String filename);

    boolean canReadFile(File file);
}
