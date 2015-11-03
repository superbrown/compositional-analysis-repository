package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.fileReader.dto.SpreadsheetData;
import gov.energy.nbc.car.model.common.Data;

import java.io.File;
import java.io.IOException;

public class FileReader extends AbsFileReader {

    public ExcelWorkbookReader excelWorkbookReader;
    public CSVFileReader csvFileReader;

    public FileReader() {

        this.excelWorkbookReader = new ExcelWorkbookReader();
        this.csvFileReader = new CSVFileReader();
    }


    public Data extractDataFromFile(File file, String nameOfWorksheetContainingTheData, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        Data data = null;

        try {
            if (excelWorkbookReader.canReadFile(file)) {

                data = extractDataFromSpreadsheet(file, nameOfWorksheetContainingTheData);
            }
            else if (csvFileReader.canReadFile(file)) {

                data = extractDataFromCSVFile(file, maxNumberOfValuesPerRow);
            }
            else {
                new UnsupportedFileExtension(file.getName());
            }
        }
        catch (IOException e) {
            // FIXME: Log
            throw new RuntimeException(e);
        }

        return data;
    }

    public Data extractDataFromSpreadsheet(File file, String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader {


        SpreadsheetData spreadsheetData =
                excelWorkbookReader.extractDataFromFile(file, nameOfWorksheetContainingTheData);

        Data data = new Data(spreadsheetData.columnNames, spreadsheetData.spreadsheetData);

        return data;
    }

    public Data extractDataFromCSVFile(File file, int maxNumberOfValuesPerRow)
            throws UnsupportedFileExtension, IOException, InvalidValueFoundInHeader {

        SpreadsheetData spreadsheetData = csvFileReader.extractDataFromFile(file, maxNumberOfValuesPerRow);

        Data data = new Data(spreadsheetData.columnNames, spreadsheetData.spreadsheetData);

        return data;
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
