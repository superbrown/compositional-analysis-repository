package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.fileReader.dto.RowCollection;
import gov.energy.nbc.car.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.exception.UnsupportedFileExtension;
import gov.energy.nbc.car.model.IRowCollection;

import java.io.File;
import java.io.IOException;

public class DatasetReader_AllFileTypes extends AbsDatasetReader implements IDatasetReader_AllFileTypes {

    public IDatasetReader_ExcelWorkbook excelWorkbookReader;
    public IDatasetReader_CSVFile csvFileReader;

    public DatasetReader_AllFileTypes() {

        this.excelWorkbookReader = new DatasetReader_ExcelWorkbook();
        this.csvFileReader = new DatasetReader_CSVFile();
    }


    @Override
    public IRowCollection extractDataFromFile(File file, String nameOfWorksheetContainingTheData, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        IRowCollection rowCollection = null;

        try {
            if (excelWorkbookReader.canReadFile(file)) {

                rowCollection = extractDataFromDataset(file, nameOfWorksheetContainingTheData);
            }
            else if (csvFileReader.canReadFile(file)) {

                rowCollection = extractDataFromCSVFile(file, maxNumberOfValuesPerRow);
            }
            else {
                new UnsupportedFileExtension(file.getName());
            }
        }
        catch (IOException e) {
            // FIXME: Log
            throw new RuntimeException(e);
        }

        return rowCollection;
    }

    @Override
    public IRowCollection extractDataFromDataset(File file, String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader {


        RowCollection dataUpload =
                excelWorkbookReader.extractDataFromFile(file, nameOfWorksheetContainingTheData);

        IRowCollection rowCollection =
                new gov.energy.nbc.car.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        return rowCollection;
    }

    public IRowCollection extractDataFromCSVFile(File file, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader {

        RowCollection dataUpload = csvFileReader.extractDataFromFile(file, maxNumberOfValuesPerRow);

        IRowCollection rowCollection = new gov.energy.nbc.car.model.mongodb.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        return rowCollection;
    }

    @Override
    public boolean isAnExcelFile(String filename) {

        return excelWorkbookReader.canReadFileWithExtension(filename);
    }

    @Override
    public boolean canReadFile(File file) {

        return excelWorkbookReader.canReadFile(file) ||
                csvFileReader.canReadFile(file);
    }

    @Override
    public boolean canReadFileWithExtension(String filename) {

        return excelWorkbookReader.canReadFileWithExtension(filename) ||
                csvFileReader.canReadFileWithExtension(filename);
    }
}
