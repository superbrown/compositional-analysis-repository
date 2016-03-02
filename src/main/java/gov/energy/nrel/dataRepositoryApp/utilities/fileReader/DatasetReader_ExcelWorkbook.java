package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Row;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import gov.energy.nrel.dataRepositoryApp.utilities.ValueSanitizer;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.dto.RowCollection;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.FileContainsInvalidColumnName;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.NotAnExcelWorkbook;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatasetReader_ExcelWorkbook extends AbsDatasetReader implements IDatasetReader_ExcelWorkbook {

    protected static Logger log = Logger.getLogger(DatasetReader_ExcelWorkbook.class);
    private PoiUtils poiUtils;


    public DatasetReader_ExcelWorkbook(ValueSanitizer valueSanitizer) {

        super(valueSanitizer);
        this.poiUtils = new PoiUtils(valueSanitizer);
    }

    @Override
    public boolean canReadFile(File file) {

        return canReadFileWithExtension(file.getName());
    }

    @Override
    public boolean canReadFileWithExtension(String fileName) {

        return Utilities.hasAnExcelFileExtension(fileName);
    }

    @Override
    public RowCollection extractDataFromFile(File file, String nameOfSubdocumentContainingDataIfApplicable)
            throws IOException, FileContainsInvalidColumnName, NotAnExcelWorkbook, UnsanitaryData {

        InputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);

            Workbook workbook = createWorkbookObject(fileInputStream, file.getName());
            Sheet sheet = workbook.getSheet(nameOfSubdocumentContainingDataIfApplicable);

            List<String> columnNames = determineColumnNames(sheet);
            int numberOfColumnNames = columnNames.size();

            List<List> data = extractData(sheet, numberOfColumnNames);

            // DESIGN NOTE: When the data was extracted, the the row number was added as the first data element so users
            //              will be able to trace the data back to the original source document.  So we need to add a
            //              name for that column.
            columnNames.add(0, Row.MONGO_KEY__ROW_NUMBER);

            RowCollection spreasheetData = new RowCollection(columnNames, data);

            return spreasheetData;
        }
        finally {

            try {

                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            finally {
                // Garbage collect because we're having problems with the worksheet
                // staying in memory, and it's often HUGE!
                System.gc();
            }
        }
    }

    protected List<List> extractData(Sheet sheet, int numberOfColumnNames)
            throws UnsanitaryData {

        List<List> dataUpload = new ArrayList<>();
        Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.rowIterator();

        // This is to be interpreted as the row containing the column names.
        iterateToTheFirstRowWithAValueInItsFirstCell(rowIterator);

        while (rowIterator.hasNext()) {

            org.apache.poi.ss.usermodel.Row row = rowIterator.next();

            List<Object> rowData = null;
            try {
                rowData = extractData(row, numberOfColumnNames);
            }
            catch (UnsanitaryData e) {
                // adding 1 because rows are zero indexed
                e.rowNumber = row.getRowNum() + 1;
                throw e;
            }

            List<Object> allDataInRowExceptTheFirstColumnThatContainsTheRowNumber =
                    rowData.subList(1, rowData.size());

            if (containsData(allDataInRowExceptTheFirstColumnThatContainsTheRowNumber)) {
                dataUpload.add(rowData);
            }
        }

        return dataUpload;
    }

    protected List<Object> extractData(org.apache.poi.ss.usermodel.Row row, int numberOfColumnHeadings)
            throws UnsanitaryData {

        List<Object> rowData = new ArrayList<>();

        // DESIGN NOTE: We are added the row number number so users will be able to trace the data back to the original
        //              source document.
        int rowNumber = row.getRowNum() + 1; // This appears to be zero indexed.
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

            Object value = null;
            try {
                value = poiUtils.toAppropriateDataType(cellData);
            }
            catch (UnsanitaryData e) {
                e.columnNumber = columnIndex + 1;
                throw e;
            }

            rowData.add(value);

            columnIndex++;
        }

        addBlankCellsToTheEndIfNecessary(rowData, columnIndex, numberOfColumnHeadings);

        return rowData;
    }

    public static Workbook createWorkbookObject(InputStream fileInputStream, String filePath)
            throws IOException, NotAnExcelWorkbook {

        try {
            if (filePath.toLowerCase().endsWith(".xls")) {

                return new HSSFWorkbook(fileInputStream);
            }
            else if (filePath.toLowerCase().endsWith(".xlsx") ||
                     filePath.toLowerCase().endsWith(".xlsm")) {

                return new XSSFWorkbook(fileInputStream);
            }

            String fileName = filePath.substring(filePath.lastIndexOf('/'));
            throw new NotAnExcelWorkbook(fileName);
        }
        finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
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


    protected List<String> determineColumnNames(Sheet sheet)
            throws FileContainsInvalidColumnName, UnsanitaryData {

        Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.rowIterator();

        // This is to be interpreted as the row containing the column names.
        org.apache.poi.ss.usermodel.Row firstRowWithAValueInItsFirstCell =
                iterateToTheFirstRowWithAValueInItsFirstCell(rowIterator);
        Iterator<Cell> cellIterator = firstRowWithAValueInItsFirstCell.cellIterator();

        List<String> columnNames = new ArrayList<>();

        boolean lastColumnEncountered = false;
        int columnNumber = 1;
        while (cellIterator.hasNext()) {

            Cell cell = cellIterator.next();
            int cellType = cell.getCellType();

            switch (cellType) {

                case Cell.CELL_TYPE_STRING:
                    String stringCellValue = cell.getStringCellValue();
                    if (valueSanitizer.isSanitary(stringCellValue) == false) {
                        String sanitizedValue = valueSanitizer.sanitize(stringCellValue);
                        UnsanitaryData unsanitaryData = new UnsanitaryData(sanitizedValue);
                        // adding 1 because rows are zero indexed
                        unsanitaryData.rowNumber = firstRowWithAValueInItsFirstCell.getRowNum() + 1;
                        unsanitaryData.columnNumber = columnNumber;
                        throw unsanitaryData;
                    }
                    columnNames.add(stringCellValue);
                    break;

                case Cell.CELL_TYPE_NUMERIC:
                    columnNames.add(String.valueOf(cell.getNumericCellValue()));
                    break;

                case Cell.CELL_TYPE_BLANK:
                    lastColumnEncountered = true;
                    break;

                case Cell.CELL_TYPE_BOOLEAN:
                case Cell.CELL_TYPE_ERROR:
                case Cell.CELL_TYPE_FORMULA:
                    throw new FileContainsInvalidColumnName(columnNumber, cell.toString());
            }

            if (lastColumnEncountered) {
                break;
            }

            columnNumber++;
        }

        return columnNames;
    }

    public static org.apache.poi.ss.usermodel.Row iterateToTheFirstRowWithAValueInItsFirstCell(
            Iterator<org.apache.poi.ss.usermodel.Row> rowIterator) {

        while (true) {
            org.apache.poi.ss.usermodel.Row row = rowIterator.next();
            Cell firstCell = row.getCell(0);
            if (StringUtils.isNotEmpty(firstCell.getStringCellValue())) {
                return row;
            }
        }
    }
}
