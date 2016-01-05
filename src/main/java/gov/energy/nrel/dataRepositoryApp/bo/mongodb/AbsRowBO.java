package gov.energy.nrel.dataRepositoryApp.bo.mongodb;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.ServletContainerConfig;
import gov.energy.nrel.dataRepositoryApp.bo.IRowBO;
import gov.energy.nrel.dataRepositoryApp.bo.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.dao.IRowDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator;
import gov.energy.nrel.dataRepositoryApp.dao.dto.SearchCriterion;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.model.IRowDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Row;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.RowDocument;
import gov.energy.nrel.dataRepositoryApp.restEndpoint.DataType;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.utilities.PerformanceLogger;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbsRowBO implements IRowBO {

    private static final String ATTR_SOURCE_UUID = " Source UUID";
    private static final String ATTR_ROW_UUID = " Row UUID";
    protected Logger log = Logger.getLogger(getClass());

    private static final String ATTR_ORIGINAL_FILE_NAME = " Original File Name";
    private static final String ATTR_ORIGINAL_FILE_ROW_NUMBER = " Original File Row Number";

    protected IRowDAO rowDAO;

    public AbsRowBO(ISettings settings) {

        init(settings);
    }

    protected abstract void init(ISettings settings);

    @Override
    public String getRow(String rowId) {

        IRowDocument rowDocument = getRowDAO().get(rowId);

        if (rowDocument == null) { return null; }

        String json = DAOUtilities.serialize(rowDocument);
        return json;
    }

    @Override
    public String getRows(String query, ResultsMode resultsMode) {

        List<Document> rowDocuments = getRowsAsDocuments(query, resultsMode);

        String json = DAOUtilities.serialize(rowDocuments);
        return json;
    }

    protected List<Document> getRowsAsDocuments(String query, ResultsMode resultsMode) {

        BasicDBList criteria = (BasicDBList) JSON.parse(query);

        List<SearchCriterion> rowSearchCriteria = new ArrayList<>();

        for (Object criterion : criteria) {

            BasicDBObject criterionDocument = (BasicDBObject)criterion;

            String name = (String) criterionDocument.get("name");
            String dataTypeString = (String) criterionDocument.get("dataType");
            Object dataType = DataType.valueOf(dataTypeString);
            Object rawValue = criterionDocument.get("value");
            ComparisonOperator comparisonOperator = ComparisonOperator.valueOf((String) criterionDocument.get("comparisonOperator"));

            Object value = null;
            if (dataType == DataType.STRING) {
                String aString = rawValue.toString();
                value = aString;
            }
            else if (dataType == DataType.NUMBER) {
                Double aDouble = new Double(rawValue.toString());
                value = aDouble;
            }
            else if (dataType == DataType.DATE) {

                value = toDateWithTheTimeOfDayAdjustedSoComparisonOperatesCorrectly(
                        rawValue, comparisonOperator);
            }
            else if (dataType == DataType.BOOLEAN) {
                Boolean aBoolean = Boolean.valueOf(rawValue.toString());
                value = aBoolean;
            }
            else {
                throw new RuntimeException("Unrecognized data type: " + dataType);
            }

            SearchCriterion searchCriterion = new SearchCriterion(name, value, comparisonOperator);
            if (searchCriterion.containsEverthingNeededToDefineASearchFilter() == false) {
                continue;
            }

            if (value instanceof Date && comparisonOperator == ComparisonOperator.EQUALS) {

                List<SearchCriterion> searchCriteria =
                        crateSearchCriteriaToMakeSureWholeDayIsCovered(name, (Date) value);
                rowSearchCriteria.addAll(searchCriteria);
            }
            else {
                rowSearchCriteria.add(searchCriterion);
            }
        }

        if (rowSearchCriteria.size() == 0) {
            return new ArrayList<>();
        }

        return getRowDAO().query(rowSearchCriteria, resultsMode);
    }

    protected List<SearchCriterion> crateSearchCriteriaToMakeSureWholeDayIsCovered(String name, Date date) {

        Calendar beginningOfTheDay = Utilities.toCalendar(date);
        Utilities.setTimeToTheBeginningOfTheDay(beginningOfTheDay);

        Calendar endOfTheDay = Utilities.clone(beginningOfTheDay);
        Utilities.setTimeToTheEndOfTheDay(endOfTheDay);

        List<SearchCriterion> searchCriteria = new ArrayList<>();

        SearchCriterion laterThanBeginningOfTheDay =
                new SearchCriterion(name, beginningOfTheDay.getTime(), ComparisonOperator.GREATER_THAN_OR_EQUAL);
        searchCriteria.add(laterThanBeginningOfTheDay);

        SearchCriterion earlierThanTheEndOfDay = new SearchCriterion(
                name, endOfTheDay.getTime(), ComparisonOperator.LESS_THAN_OR_EQUAL);
        searchCriteria.add(earlierThanTheEndOfDay);

        return searchCriteria;
    }

    protected Date toDateWithTheTimeOfDayAdjustedSoComparisonOperatesCorrectly(Object rawValue, ComparisonOperator comparisonOperator) {

        Calendar calendar = Utilities.toCalendar(rawValue.toString());
        adjustTimeOfDaySoComparisonsOperatesCorrectly(calendar, comparisonOperator);
        return calendar.getTime();
    }

    protected void adjustTimeOfDaySoComparisonsOperatesCorrectly(Calendar calendar, ComparisonOperator comparisonOperator) {

        if (comparisonOperator == ComparisonOperator.GREATER_THAN_OR_EQUAL) {
            Utilities.setTimeToTheBeginningOfTheDay(calendar);
        }
        else if (comparisonOperator == ComparisonOperator.LESS_THAN_OR_EQUAL) {
            Utilities.setTimeToTheEndOfTheDay(calendar);
        }
        else if (comparisonOperator == ComparisonOperator.GREATER_THAN) {
            Utilities.setTimeToTheEndOfTheDay(calendar);
        }
        else if (comparisonOperator == ComparisonOperator.LESS_THAN) {
            Utilities.setTimeToTheBeginningOfTheDay(calendar);
        }
    }

    @Override
    public String getRowsFlat(String query) {

        List<Document> rowDocuments = getRowsAsDocuments(
                query,
                ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);

        BasicDBList rowsFlat = flatten(rowDocuments, Purpose.FOR_SCREEN_DIAPLAYED_SEARCH_RESULTS);

        String json = DAOUtilities.serialize(rowsFlat);
        return json;
    }

    private enum Purpose {
        FOR_FILE_DOWNLOAD,
        FOR_SCREEN_DIAPLAYED_SEARCH_RESULTS,
    }

    private BasicDBList flatten(List<Document> rowDocuments, Purpose purpose) {

        BasicDBList rowsFlat = new BasicDBList();

        for (Document document : rowDocuments) {

            Document row = new Document();

            row.put(ATTR_SOURCE_UUID, getObjectId(document, RowDocument.ATTR_KEY__DATASET_ID));

            if (purpose == Purpose.FOR_FILE_DOWNLOAD) {
                // including in case someone might find it helpful to have
                row.put(ATTR_ROW_UUID, getObjectId(document, RowDocument.ATTR_KEY__ID));
            }

            Document metadata = (Document) document.get(RowDocument.ATTR_KEY__METADATA);
            Document data = (Document) document.get(RowDocument.ATTR_KEY__DATA);
            String datasetId = getObjectId(document, RowDocument.ATTR_KEY__DATASET_ID);

            Document sourceDocument = (Document) metadata.get(Metadata.ATTR_KEY__SOURCE_DOCUMENT);

            String originalFileName = (String) sourceDocument.get(StoredFile.ATTR_KEY__ORIGINAL_FILE_NAME);
            Integer rowNumber = (Integer) data.get(Row.ATTR_KEY__ROW_NUMBER);

            Object nameOfSubdocumentContainingDataIfApplicable = metadata.get(Metadata.ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA);
            if (StringUtils.isBlank((String) nameOfSubdocumentContainingDataIfApplicable)) {
                nameOfSubdocumentContainingDataIfApplicable = "N/A";
            }

            if (purpose == Purpose.FOR_FILE_DOWNLOAD) {

                row.put(ATTR_ORIGINAL_FILE_NAME, originalFileName);
                row.put(Metadata.ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA, nameOfSubdocumentContainingDataIfApplicable);
                row.put(ATTR_ORIGINAL_FILE_ROW_NUMBER, rowNumber);
                row.put(Metadata.ATTR_KEY__DATA_CATEGORY, metadata.get(Metadata.ATTR_KEY__DATA_CATEGORY));
            }
            else if (purpose == Purpose.FOR_SCREEN_DIAPLAYED_SEARCH_RESULTS) {

                // link for downloading the file
                // DESIGN NOTE: I know, this is the wrong architectural layer. I'm in a time crunch right now.
                row.put("Source Document",
                        "<a href='" + ServletContainerConfig.CONTEXT_PATH +
                                "/api/dataset/" + datasetId + "/sourceDocument' " +
                                "target='_blank'>" +
                                originalFileName + "</a> (row " + rowNumber + ")");

                row.put(Metadata.ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA,
                        nameOfSubdocumentContainingDataIfApplicable);
            }

            row.put(Metadata.ATTR_KEY__SUBMISSION_DATE, toString((Date) metadata.get(Metadata.ATTR_KEY__SUBMISSION_DATE)));
            row.put(Metadata.ATTR_KEY__SUBMITTER, metadata.get(Metadata.ATTR_KEY__SUBMITTER));
            row.put(Metadata.ATTR_KEY__PROJECT_NAME, metadata.get(Metadata.ATTR_KEY__PROJECT_NAME));
            row.put(Metadata.ATTR_KEY__CHARGE_NUMBER, metadata.get(Metadata.ATTR_KEY__CHARGE_NUMBER));
            row.put(Metadata.ATTR_KEY__COMMENTS, metadata.get(Metadata.ATTR_KEY__COMMENTS));

            Set<String> columnNames = data.keySet();
            columnNames = Utilities.toSortedSet(columnNames);

            for (String name : columnNames) {

                Object value = data.get(name);

                if (value == null) {

                }
                else if (Row.ATTR_KEY__ROW_NUMBER.equals(name)) {
                    // we already grabbed this above
                }
                else {
                    if (purpose == Purpose.FOR_SCREEN_DIAPLAYED_SEARCH_RESULTS) {

                        if (value instanceof ObjectId) {
                            row.put(name, ((ObjectId) value).toHexString());
                        }
                        else if (value instanceof Number) {
                            String stringValue = value.toString();
                            row.put(name, stringValue);
                        }
                        else if (value instanceof Date) {
                            row.put(name, toString((Date) value));
                        }
                        else {
                            row.put(name, value.toString());
                        }
                    }
                    else {
                        row.put(name, value);
                    }
                }
            }

            if (purpose == Purpose.FOR_SCREEN_DIAPLAYED_SEARCH_RESULTS) {

                List attachments = (List) metadata.get(Metadata.ATTR_KEY__ATTACHMENTS);
                String originalFileNames = toOriginalFileNames(attachments);

                if (StringUtils.isNotBlank(originalFileNames)) {
                    row.put("Attachments",
                            "<a href='" + ServletContainerConfig.CONTEXT_PATH +
                                    "/api/dataset/" + datasetId + "/attachments' " +
                                    "target='_blank'>" +
                                    originalFileNames + "</a>");
                }
                else {
                    row.put("Attachments",
                            "(none)");
                }
            }

            rowsFlat.add(row);
        }

        return rowsFlat;
    }

    private String toOriginalFileNames(List attachments) {

        String originalFileNames = "";
        for (Object attachment : attachments) {
            Document document = (Document) attachment;
            Object originalFilename = document.get(StoredFile.ATTR_KEY__ORIGINAL_FILE_NAME);
            originalFileNames += "<br>" + originalFilename;
        }
        originalFileNames = originalFileNames.replaceFirst("<br>", "");
        return originalFileNames;
    }

    private String getObjectId(Document document, String attributeName) {
        return ((ObjectId)document.get(attributeName)).toHexString();
    }

    @Override
    public XSSFWorkbook getRowsAsExcelWorkbook(String query) {

        List<Document> documents = getRowsAsDocuments(query, ResultsMode.INCLUDE_ALL_DATA);

        BasicDBList basicDBList = flatten(documents, Purpose.FOR_FILE_DOWNLOAD);

        XSSFWorkbook workbook = toExcelWorkbook(basicDBList, query);
        return workbook;
    }

    private static final List<String> METADATA_COLUMNS_TO_RETURN = new ArrayList();
    static {
        METADATA_COLUMNS_TO_RETURN.add(ATTR_SOURCE_UUID);
        METADATA_COLUMNS_TO_RETURN.add(ATTR_ROW_UUID);
        METADATA_COLUMNS_TO_RETURN.add(ATTR_ORIGINAL_FILE_NAME);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA);
        METADATA_COLUMNS_TO_RETURN.add(ATTR_ORIGINAL_FILE_ROW_NUMBER);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.ATTR_KEY__DATA_CATEGORY);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.ATTR_KEY__SUBMISSION_DATE);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.ATTR_KEY__SUBMITTER);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.ATTR_KEY__PROJECT_NAME);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.ATTR_KEY__CHARGE_NUMBER);
        METADATA_COLUMNS_TO_RETURN.add(Metadata.ATTR_KEY__COMMENTS);
    }

    private XSSFWorkbook toExcelWorkbook(BasicDBList documents, String query) {

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

        XSSFSheet worksheet = workbook.createSheet("sheet");

        // create heading row
        short rowIndex = 0;

        // inital blank row
        worksheet.createRow(rowIndex++);
        worksheet.createRow(rowIndex++);

        XSSFRow row = worksheet.createRow(rowIndex++);

        int columnIndex = 0;

        XSSFCellStyle italicBoldStyle = getItalicBoldStyle(workbook);

        for (String columnName : allKeys) {
            XSSFCell cell = row.createCell(columnIndex);
            cell.setCellValue(columnName);
            cell.setCellStyle(italicBoldStyle);
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

    private void setAsActiveCell(XSSFSheet worksheet, int rowNumber, int collumnNumber) {

        XSSFRow firstDataRow = worksheet.getRow(rowNumber);
        firstDataRow.getCell(collumnNumber).setAsActiveCell();
    }

    private String toHumanReadableFilterString(String query) {

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

    private void putQueryInCell(XSSFSheet worksheet, String string, int rowNumber, int columnNumber) {

        XSSFRow firstRow = worksheet.getRow(rowNumber);
        XSSFCell firstCell = firstRow.createCell(columnNumber);
        firstCell.setCellValue("Filter: " + string);
        firstCell.setCellStyle(getBlueStyle(worksheet.getWorkbook()));
    }

    private XSSFCellStyle getItalicBoldStyle(XSSFWorkbook workbook) {

        XSSFFont boldFont= workbook.createFont();
        boldFont.setBold(true);
        boldFont.setItalic(true);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(boldFont);

        return style;
    }

    private XSSFCellStyle getBlueStyle(XSSFWorkbook workbook) {

        XSSFFont boldFont= workbook.createFont();
        boldFont.setColor(IndexedColors.BLUE.getIndex());

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(boldFont);

        return style;
    }

    private void setCellValue(XSSFCell cell, Object value) {
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

    private Set<String> extractAllKeys(BasicDBList documents) {

        Set<String> allUniqueKeys = new HashSet<>();
        for (Object document : documents) {
            allUniqueKeys.addAll(((Document)document).keySet());
        }

        return allUniqueKeys;
    }

//    @Override
//    public String getRows(RowSearchCriteria rowSearchCriteria, String projection) {
//
//        List<Document> rowDocuments = getRowDAO(testMode).query(rowSearchCriteria);
//
//        if (rowDocuments.size() == 0) { return null; }
//
//        String json = DAOUtilities.serialize(rowDocuments);
//        return json;
//    }

//    public String getRows(Bson query, Bson projection) {
//
//        List<Document> rowDocuments = getRowDAO(testMode).query(query);
//
//        if (rowDocuments.size() == 0) { return null; }
//
//        String json = DAOUtilities.serialize(rowDocuments);
//        return json;
//    }

    @Override
    public String getRows(List<SearchCriterion> rowSearchCriteria, ResultsMode resultsMode) {

        List<Document> rowDocuments = getRowDAO().query(rowSearchCriteria, resultsMode);

        if (rowDocuments.size() == 0) { return null; }

        String json = DAOUtilities.serialize(rowDocuments);
        return json;
    }

    @Override
    public String getAllRows() {

        Iterable<Document> rowDocuments = getRowDAO().getAll();

        String jsonOut = DAOUtilities.serialize(rowDocuments);
        return jsonOut;
    }


    @Override
    public String getRowsAssociatedWithDataset(String datasetId) {

        Document idFilter = new Document().append(
                RowDocument.ATTR_KEY__DATASET_ID, new ObjectId(datasetId));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "getRows(testMode).query(" + idFilter.toJson() + ")");
        List<Document> rowDocuments = getRowDAO().get(idFilter, null);
        performanceLogger.done();

        String jsonOut = DAOUtilities.serialize(rowDocuments);
        return jsonOut;
    }

    @Override
    public IRowDAO getRowDAO() {
        return rowDAO;
    }

    protected String toString(Date date) {
        String string = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return string;
    }
}
