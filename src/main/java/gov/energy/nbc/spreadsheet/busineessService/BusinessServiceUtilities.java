package gov.energy.nbc.spreadsheet.busineessService;

import gov.energy.nbc.spreadsheet.fileReader.ExcelSpreadsheetReader;
import gov.energy.nbc.spreadsheet.fileReader.NonStringValueFoundInHeader;
import gov.energy.nbc.spreadsheet.fileReader.UnsupportedFileExtension;
import gov.energy.nbc.spreadsheet.model.common.Data;
import gov.energy.nbc.spreadsheet.utilities.SpreadsheetData;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BusinessServiceUtilities {

    public ExcelSpreadsheetReader excelSpreadsheetReader;

    public BusinessServiceUtilities() {

        this.excelSpreadsheetReader = new ExcelSpreadsheetReader();
    }

    public Data extractDataFromSpreadsheet(File file, String nameOfWorksheetContainingTheData)
            throws UnsupportedFileExtension, IOException, NonStringValueFoundInHeader {

        SpreadsheetData spreadsheetData =
                excelSpreadsheetReader.extractDataFromSpreadsheet(file, nameOfWorksheetContainingTheData);

        List<Object> columnNamesAsAGenericList = Arrays.asList(spreadsheetData.columnNames.toArray());

        Data data = new Data(columnNamesAsAGenericList, spreadsheetData.spreadsheetData);

        return data;
    }
}
