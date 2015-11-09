package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.fileReader.dto.RowCollection;

import java.io.File;
import java.io.IOException;

public class FileReader extends AbsFileReader {

    public ExcelWorkbookReader excelWorkbookReader;
    public CSVFileReader csvFileReader;

    public FileReader() {

        this.excelWorkbookReader = new ExcelWorkbookReader();
        this.csvFileReader = new CSVFileReader();
    }


    public gov.energy.nbc.car.model.common.RowCollection extractDataFromFile(File file, String nameOfWorksheetContainingTheData, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        gov.energy.nbc.car.model.common.RowCollection rowCollection = null;

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

    public gov.energy.nbc.car.model.common.RowCollection extractDataFromDataset(File file, String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader {


        RowCollection dataUpload =
                excelWorkbookReader.extractDataFromFile(file, nameOfWorksheetContainingTheData);

        gov.energy.nbc.car.model.common.RowCollection rowCollection = new gov.energy.nbc.car.model.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        return rowCollection;
    }

    public gov.energy.nbc.car.model.common.RowCollection extractDataFromCSVFile(File file, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader {

        RowCollection dataUpload = csvFileReader.extractDataFromFile(file, maxNumberOfValuesPerRow);

        gov.energy.nbc.car.model.common.RowCollection rowCollection = new gov.energy.nbc.car.model.common.RowCollection(dataUpload.columnNames, dataUpload.rowData);

        return rowCollection;
    }

    @Override
    public boolean canReadFile(File file) {

        return canReadFileWithExtension(file.getName());
    }

    @Override
    public boolean canReadFileWithExtension(String fileName) {

        return (excelWorkbookReader.canReadFileWithExtension(fileName) &&
                csvFileReader.canReadFileWithExtension(fileName));
    }
}
