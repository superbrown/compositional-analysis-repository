package gov.energy.nbc.spreadsheet.documents;

import org.bson.Document;

public class SpreadsheetRowData extends Document {

    public static final String ATTRIBUTE_KEY__ROW_NUMBER = "rowNumber";

    public SpreadsheetRowData(Object[] columnNames, int rowIndex, Object[] rowValues) {

        put(ATTRIBUTE_KEY__ROW_NUMBER, rowIndex);

        for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {

            Object columnName = columnNames[columnIndex];
            Object columnValue = rowValues[columnIndex];
            put(columnName.toString(), columnValue);
        }
    }
}
