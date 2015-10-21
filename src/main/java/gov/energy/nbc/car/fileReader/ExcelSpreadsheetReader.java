package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.utilities.SpreadsheetData;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelSpreadsheetReader {

    protected Logger log = Logger.getLogger(this.getClass());


    public static SpreadsheetData extractDataFromSpreadsheet(File file, String nameOfWorksheetContainingTheData)
            throws IOException, NonStringValueFoundInHeader, UnsupportedFileExtension {

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);

            Workbook workbook = createWorkbookObject(fileInputStream, file.getName());
            Sheet sheet = workbook.getSheet(nameOfWorksheetContainingTheData);

            List<String> columnNames = PoiUtils.determineColumnNames(sheet);
            int numberOfColumnHeadings = columnNames.size();

            List<List> data = extractData(sheet, numberOfColumnHeadings);

            SpreadsheetData spreasheetData = new SpreadsheetData(columnNames, data);
            return spreasheetData;
        }
        finally {

            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    protected static List<List> extractData(Sheet sheet, int numberOfColumnHeadings) {

        List<List> spreadsheetData = new ArrayList();
        Iterator<Row> rowIterator = sheet.rowIterator();

        boolean isFirstRow = true;
        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            List<Object> rowData = extractData(row, numberOfColumnHeadings);

            spreadsheetData.add(rowData);
        }

        return spreadsheetData;
    }

    protected static List<Object> extractData(Row row, int numberOfColumnHeadings) {

        List<Object> rowData = new ArrayList();

        //Get iterator to all cells of current row
        Iterator<Cell> cellIterator = row.cellIterator();

        int columnIndex = 0;
        while (cellIterator.hasNext()) {

            Cell cellData = cellIterator.next();
            int indexOfColumnCellIsIn = cellData.getColumnIndex();

            if (indexOfColumnCellIsIn >= numberOfColumnHeadings) {
                break;
            }

            columnIndex = addBlankCellsIfSomeWereSkippedByPoi(rowData, columnIndex, indexOfColumnCellIsIn);

            if (columnIndex >= numberOfColumnHeadings) {
                break;
            }

            Object value = PoiUtils.toAppropriateDataType(cellData);

            rowData.add(value);

            columnIndex++;
        }

        addBlankCellsToTheEndIfNecessary(rowData, columnIndex, numberOfColumnHeadings);

        return rowData;
    }

    protected static Workbook createWorkbookObject(FileInputStream fileInputStream, String filePath)
            throws IOException, UnsupportedFileExtension {

        // Get the workbook instance for XLS file

        if (filePath.toLowerCase().endsWith(".xls")) {

            return new HSSFWorkbook(fileInputStream);
        }
        else if (filePath.toLowerCase().endsWith(".xlsx") ||
                 filePath.toLowerCase().endsWith(".xlsm")) {

            return new XSSFWorkbook(fileInputStream);
        }

        throw new UnsupportedFileExtension(filePath);
    }


    protected static int addBlankCellsIfSomeWereSkippedByPoi(List<Object> rowData, int columnIndex, int indexOfColumnCellIsIn) {

        if (indexOfColumnCellIsIn > columnIndex) {

            rowData.add(null);
            columnIndex++;

            columnIndex = addBlankCellsIfSomeWereSkippedByPoi(rowData, columnIndex, indexOfColumnCellIsIn);
        }

        return columnIndex;
    }

    protected static void addBlankCellsToTheEndIfNecessary(List<Object> rowData, int numberOfDataCells, int numberOfColumnHeadings) {

        for (int i = numberOfDataCells; i < numberOfColumnHeadings; i++) {

            rowData.add(null);
        }
    }
}
