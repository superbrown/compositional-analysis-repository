package gov.energy.nbc.car.fileReader;

import gov.energy.nbc.car.fileReader.dto.SpreadsheetData;
import gov.energy.nbc.car.model.common.SpreadsheetRow;
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

public class ExcelWorkbookReader extends AbsFileReader {

    protected Logger log = Logger.getLogger(this.getClass());

    @Override
    public boolean canReadFile(File file) {

        return canReadFileWithExtension(file.getName());
    }

    @Override
    public boolean canReadFileWithExtension(String fileName) {

        fileName = fileName.toLowerCase();
        return (fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".xlsm")) == true;
    }

    public SpreadsheetData extractDataFromFile(File file, String nameOfWorksheetContainingTheData)
            throws IOException, InvalidValueFoundInHeader, UnsupportedFileExtension {

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);

            Workbook workbook = createWorkbookObject(fileInputStream, file.getName());
            Sheet sheet = workbook.getSheet(nameOfWorksheetContainingTheData);

            List<String> columnNames = determineColumnNames(sheet);
            int numberOfColumnNames = columnNames.size();

            List<List> data = extractData(sheet, numberOfColumnNames);

            // DESIGN NOTE: When the data was extracted, the the row number was added as the first data element so users
            //              will be able to trace the data back to the original source document.  So we need to add a
            //              name for that column.
            columnNames.add(0, SpreadsheetRow.ATTRIBUTE_KEY__ROW_NUMBER);

            SpreadsheetData spreasheetData = new SpreadsheetData(columnNames, data);
            return spreasheetData;
        }
        finally {

            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    protected List<List> extractData(Sheet sheet, int numberOfColumnHeadings) {

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

            List<Object> allDataInRowExceptTheFirstColumnThatContainsTheRowNumber =
                    rowData.subList(1, rowData.size());

            if (containsData(allDataInRowExceptTheFirstColumnThatContainsTheRowNumber)) {
                spreadsheetData.add(rowData);
            }
        }

        return spreadsheetData;
    }

    protected List<Object> extractData(Row row, int numberOfColumnHeadings) {

        List<Object> rowData = new ArrayList();

        // DESIGN NOTE: We are added the row number number so users will be able to trace the data back to the original
        //              source document.
        int rowNumber = row.getRowNum();
        rowData.add(rowNumber);

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

    protected Workbook createWorkbookObject(FileInputStream fileInputStream, String filePath)
            throws IOException, UnsupportedFileExtension {

        if (filePath.toLowerCase().endsWith(".xls")) {

            return new HSSFWorkbook(fileInputStream);
        }
        else if (filePath.toLowerCase().endsWith(".xlsx") ||
                 filePath.toLowerCase().endsWith(".xlsm")) {

            return new XSSFWorkbook(fileInputStream);
        }

        throw new UnsupportedFileExtension(filePath);
    }

    protected int addBlankCellsIfSomeWereSkippedByPoi(List<Object> rowData, int columnIndex, int indexOfColumnCellIsIn) {

        if (indexOfColumnCellIsIn > columnIndex) {

            rowData.add(null);
            columnIndex++;

            columnIndex = addBlankCellsIfSomeWereSkippedByPoi(rowData, columnIndex, indexOfColumnCellIsIn);
        }

        return columnIndex;
    }

    protected void addBlankCellsToTheEndIfNecessary(List<Object> rowData, int numberOfDataCells, int numberOfColumnHeadings) {

        for (int i = numberOfDataCells; i < numberOfColumnHeadings; i++) {

            rowData.add(null);
        }
    }


    public List<String> determineColumnNames(Sheet sheet)
            throws InvalidValueFoundInHeader {

        Row firstRow = sheet.iterator().next();
        Iterator<Cell> cellIterator = firstRow.cellIterator();

        List<String> headings = new ArrayList();

        boolean lastColumnEncountered = false;
        int columnNumber = 1;
        while (cellIterator.hasNext()) {

            Cell cell = cellIterator.next();
            int cellType = cell.getCellType();

            switch (cellType) {

                case Cell.CELL_TYPE_STRING:
                    headings.add(cell.getStringCellValue());
                    break;

                case Cell.CELL_TYPE_NUMERIC:
                    headings.add(String.valueOf(cell.getNumericCellValue()));
                    break;

                case Cell.CELL_TYPE_BLANK:
                    lastColumnEncountered = true;
                    break;

                case Cell.CELL_TYPE_BOOLEAN:
                case Cell.CELL_TYPE_ERROR:
                case Cell.CELL_TYPE_FORMULA:
                    throw new InvalidValueFoundInHeader(columnNumber, cell.toString());
            }

            if (lastColumnEncountered) {
                break;
            }

            columnNumber++;
        }

        return headings;
    }
}
