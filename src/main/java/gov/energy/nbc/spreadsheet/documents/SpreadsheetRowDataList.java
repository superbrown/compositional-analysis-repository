package gov.energy.nbc.spreadsheet.documents;

import java.util.ArrayList;

public class SpreadsheetRowDataList extends ArrayList<SpreadsheetRowData> {

    public SpreadsheetRowDataList(Object[][] spreadsheetContent) {

        super();

        Object[] columnNames = spreadsheetContent[0];

        for (int rowIndex = 1; rowIndex < spreadsheetContent.length; rowIndex++) {

            Object[] rowValues = spreadsheetContent[rowIndex];
            SpreadsheetRowData spreadsheetRowData = new SpreadsheetRowData(columnNames, rowIndex, rowValues);
            add(spreadsheetRowData);
        }
    }
}
