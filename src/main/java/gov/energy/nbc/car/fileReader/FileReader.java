package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.fileReader.dto.SpreadsheetData;
import gov.energy.nbc.car.model.common.Data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileReader implements IFileReader {

    public ExcelWorkbookReader excelWorkbookReader;
    public CSVFileReader csvFileReader;

    public FileReader() {

        this.excelWorkbookReader = new ExcelWorkbookReader();
        this.csvFileReader = new CSVFileReader();
    }


    public Data extractDataFromFile(File file, String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, NonStringValueFoundInHeader {

        Data data = null;

        try {
            if (excelWorkbookReader.canReadFile(file)) {

                data = extractDataFromSpreadsheet(file, nameOfWorksheetContainingTheData);
            }
            else if (csvFileReader.canReadFile(file)) {

                data = extractDataFromCSVFile(file);
            }
            else {
                new UnsupportedFileExtension(file.getName());
            }
        }
        catch (IOException e) {
            // FIXME: Log
        }

        return data;
    }

    public Data extractDataFromSpreadsheet(File file, String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, IOException, NonStringValueFoundInHeader {

        SpreadsheetData spreadsheetData =
                excelWorkbookReader.extractDataFromFile(file, nameOfWorksheetContainingTheData);

        List<Object> columnNamesAsAGenericList = Arrays.asList(spreadsheetData.columnNames.toArray());

        Data data = new Data(columnNamesAsAGenericList, spreadsheetData.spreadsheetData);

        return data;
    }

    public Data extractDataFromCSVFile(File file)
            throws UnsupportedFileExtension, IOException, NonStringValueFoundInHeader {

        SpreadsheetData spreadsheetData = csvFileReader.extractDataFromFile(file);

        List<Object> columnNamesAsAGenericList = Arrays.asList(spreadsheetData.columnNames.toArray());

        Data data = new Data(columnNamesAsAGenericList, spreadsheetData.spreadsheetData);

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
