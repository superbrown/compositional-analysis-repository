package gov.energy.nbc.car.fileReader.dto;

import java.util.List;

public class SpreadsheetData {

    public final List<String> columnNames;
    public final List<List> spreadsheetData;

    public SpreadsheetData(List<String> columnNames, List<List> spreadsheetData) {

        this.columnNames = columnNames;
        this.spreadsheetData = spreadsheetData;
    }
}
