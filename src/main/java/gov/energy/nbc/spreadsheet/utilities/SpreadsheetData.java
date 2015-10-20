package gov.energy.nbc.spreadsheet.utilities;

import java.util.List;

public class SpreadsheetData {

    public final List<String> columnNames;
    public final List<List<Object>> spreadsheetData;

    public SpreadsheetData(List<String> columnNames, List<List<Object>> spreadsheetData) {

        this.columnNames = columnNames;
        this.spreadsheetData = spreadsheetData;
    }
}
