package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.IRowDocument;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Row;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.bson.Document;

import java.util.*;

public class SearchResultsFileWriter_ExcelWorkbook {

    private static final int FONT_HEIGHT = 9;

    public static final List<String> METADATA_COLUMNS_TO_RETURN = new ArrayList();
    static {
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__DATA_CATEGORY);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__SUBMISSION_DATE);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__SUBMITTER);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__PROJECT_NAME);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__CHARGE_NUMBER);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__COMMENTS);
        METADATA_COLUMNS_TO_RETURN.add(IDatasetDocument.DISPLAY_FIELD__SOURCE_UUID);
        METADATA_COLUMNS_TO_RETURN.add(IRowDocument.DISPLAY_FIELD__ROW_UUID);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__SOURCE_DOCUMENT);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.MONGO_KEY__SUB_DOCUMENT_NAME);
        METADATA_COLUMNS_TO_RETURN.add(Row.MONGO_KEY__ROW_NUMBER);
    }

    public static XSSFWorkbook toExcelWorkbook(BasicDBList documents, String query) {

        List<String> allKeys = new ArrayList(extractAllKeys(documents));

        // We are removing these because we want to add them in a specific order before all the
        // other columns.  In other words, we don't want them alphabetized with the others.
        allKeys.removeAll(METADATA_COLUMNS_TO_RETURN);

        Utilities.sortAlphaNumerically(allKeys);

        // Add them back to the beginning of the list.
        allKeys.addAll(0, METADATA_COLUMNS_TO_RETURN);

        // Sample code:
        // http://www.avajava.com/tutorials/lessons/how-do-i-write-to-an-excel-file-using-poi.html

        XSSFWorkbook workbook = new XSSFWorkbook();
        int fontHeight = FONT_HEIGHT;
        setDefaultFontHeight(workbook, fontHeight);

        XSSFSheet worksheet = workbook.createSheet("sheet");

        // create heading row
        short rowIndex = 0;

        // inital blank row
        worksheet.createRow(rowIndex++);
        worksheet.createRow(rowIndex++);

        XSSFRow row = worksheet.createRow(rowIndex++);

        int columnIndex = 0;

        XSSFCellStyle style_data = createColumnHeadingStyle_data(workbook);
        XSSFCellStyle style_metadata = createColumnHeadingStyle_metadata(workbook);

        for (String columnName : allKeys) {

            XSSFCell cell = row.createCell(columnIndex);
            cell.setCellValue(columnName);

            if (METADATA_COLUMNS_TO_RETURN.contains(columnName)) {
                cell.setCellStyle(style_metadata);
            } else {
                cell.setCellStyle(style_data);
            }

            columnIndex++;
        }

        for (Object object : documents) {

            Document document = (Document) object;

            row = worksheet.createRow(rowIndex++);

            columnIndex = 0;
            for (String columnName : allKeys) {

                XSSFCell cell = row.createCell(columnIndex);

                if (document.containsKey(columnName)) {
                    setCellValue(cell, document.get(columnName));
                }
                else {
                    // don't set cell's value
                }

                columnIndex++;
            }
        }


        for (columnIndex = 0; columnIndex < allKeys.size(); columnIndex++) {
            worksheet.autoSizeColumn(columnIndex);
        }

        String humanReadableFilterString = toHumanReadableFilterString(query);

        putQueryInCell(worksheet, humanReadableFilterString, 1, 0);

        // freeze the first row
        worksheet.createFreezePane(0, 3);

        setAsActiveCell(worksheet, 3, 0);

        return workbook;
    }

    public static void setAsActiveCell(XSSFSheet worksheet, int rowNumber, int collumnNumber) {

        XSSFRow firstDataRow = worksheet.getRow(rowNumber);
        firstDataRow.getCell(collumnNumber).setAsActiveCell();
    }

    public static String toHumanReadableFilterString(String query) {

        BasicDBList basicDBList = (BasicDBList) JSON.parse(query);

        Object[] basicDBObjects = basicDBList.toArray();

        String humanReadableFilterString = "";
        for (Object o : basicDBObjects) {

            BasicDBObject basicDBObject = (BasicDBObject) o;

            Object name = basicDBObject.get("name");
            Object comparisonOperator = basicDBObject.get("comparisonOperator");
            Object dataType = basicDBObject.get("dataType");
            Object value = basicDBObject.get("value");

            if (dataType.equals("DATE")) {
                value = ((String)value).substring(0, 10);
            }
            else if (dataType.equals("STRING")) {
                value = "\"" + value + "\"";
            }
            else if (dataType.equals("NUMBER")) {
                // leave as is
            }
            else if (dataType.equals("BOOLEAN")) {
                // leave as is
            }

            humanReadableFilterString += "(\"" + name + "\" " + comparisonOperator + " " + dataType + "(" + value + ")) and ";
        }

        // remove trailing "and"
        if (humanReadableFilterString.length() > 0) {
            humanReadableFilterString =
                    humanReadableFilterString.substring(
                            0, humanReadableFilterString.length() - (" and ".length()));
        }

        return humanReadableFilterString;
    }

    public static void putQueryInCell(XSSFSheet worksheet, String string, int rowNumber, int columnNumber) {

        XSSFRow firstRow = worksheet.getRow(rowNumber);
        XSSFCell firstCell = firstRow.createCell(columnNumber);
        firstCell.setCellValue("Filter: " + string);
        firstCell.setCellStyle(getBlueStyle(worksheet.getWorkbook()));
    }

    public static XSSFCellStyle createColumnHeadingStyle_metadata(XSSFWorkbook workbook) {

        XSSFFont boldFont= workbook.createFont();
        boldFont.setBold(true);
        boldFont.setItalic(true);
        boldFont.setFontHeight(FONT_HEIGHT);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(boldFont);

        return style;
    }

    public static XSSFCellStyle createColumnHeadingStyle_data(XSSFWorkbook workbook) {

        XSSFCellStyle style = createColumnHeadingStyle_metadata(workbook);

        XSSFColor grey = new XSSFColor(new java.awt.Color(230, 230, 230));
        style.setFillForegroundColor(grey);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    public static XSSFCellStyle getBlueStyle(XSSFWorkbook workbook) {

        XSSFFont boldFont= workbook.createFont();
        boldFont.setColor(IndexedColors.BLUE.getIndex());
        boldFont.setFontHeight(FONT_HEIGHT);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(boldFont);

        return style;
    }

    public static void setCellValue(XSSFCell cell, Object value) {
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        else if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }
        else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        else if (value == null) {
            // nothing to set
        }
        else {
            throw new RuntimeException("Encountered unrecognized value: " + value);
        }
    }

    public static void setDefaultFontHeight(XSSFWorkbook workbook, int fontHeight) {

        XSSFFont defaultFont = workbook.getFontAt((short) 0);
        defaultFont.setFontHeight(fontHeight);
    }

    public static Set<String> extractAllKeys(BasicDBList documents) {

        Set<String> allUniqueKeys = new HashSet<>();
        for (Object document : documents) {
            allUniqueKeys.addAll(((Document)document).keySet());
        }

        return allUniqueKeys;
    }
}
