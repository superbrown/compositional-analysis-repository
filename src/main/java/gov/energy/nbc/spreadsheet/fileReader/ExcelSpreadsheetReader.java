package gov.energy.nbc.spreadsheet.fileReader;

import gov.energy.nbc.spreadsheet.utilities.SpreadsheetData;
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


    public SpreadsheetData extractDataFromSpreadsheet(File file, String nameOfWorksheetContainingTheData)
            throws IOException, NonStringValueFoundInHeader, UnsupportedFileExtension {

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);

            Workbook workbook = createWorkbookObject(fileInputStream, file.getName());

            Sheet sheet = workbook.getSheet(nameOfWorksheetContainingTheData);

            List<String> columnNames = PoiUtils.determineColumnNames(sheet);

            List<List<Object>> spreadsheetData = new ArrayList();
            Iterator<Row> rowIterator = sheet.rowIterator();

            boolean isFirstRow = true;
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();

                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                List<Object> rowData = new ArrayList();

                //Get iterator to all cells of current row
                Iterator<Cell> cellIterator = row.cellIterator();

                int columnNumber = 1;
                while (cellIterator.hasNext()) {

                    if (columnNumber > columnNames.size()) {
                        break;
                    }

                    Cell cellData = cellIterator.next();

                    Object value = PoiUtils.toAppropriateDataType(cellData);
                    rowData.add(value);

                    columnNumber++;
                }

                spreadsheetData.add(rowData);
            }

            SpreadsheetData spreasheetData = new SpreadsheetData(columnNames, spreadsheetData);
            return spreasheetData;
        }
        finally {

            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    private Workbook createWorkbookObject(FileInputStream fileInputStream, String filePath)
            throws IOException, UnsupportedFileExtension {

        //Get the workbook instance for XLS file

        if (filePath.toLowerCase().endsWith(".xls")) {

            return new HSSFWorkbook(fileInputStream);
        }
        else if (filePath.toLowerCase().endsWith(".xlsx")) {

            return new XSSFWorkbook(fileInputStream);
        }

        throw new UnsupportedFileExtension(filePath);
    }
}
