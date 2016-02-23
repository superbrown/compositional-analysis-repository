package gov.energy.nrel.dataRepositoryApp.bo.mongodb;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.ServletContainerConfig;
import gov.energy.nrel.dataRepositoryApp.bo.IRowBO;
import gov.energy.nrel.dataRepositoryApp.bo.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownRow;
import gov.energy.nrel.dataRepositoryApp.dao.IRowDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator;
import gov.energy.nrel.dataRepositoryApp.dao.dto.SearchCriterion;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.IRowDocument;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Row;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.RowDocument;
import gov.energy.nrel.dataRepositoryApp.restEndpoint.DataType;
import gov.energy.nrel.dataRepositoryApp.utilities.PerformanceLogger;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbsRowBO extends AbsBO implements IRowBO {

    protected static Logger log = Logger.getLogger(AbsRowBO.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    protected IRowDAO rowDAO;

    public AbsRowBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    public String getRow(String rowId)
            throws UnknownRow {

        IRowDocument rowDocument = getRowDAO().get(rowId);

        if (rowDocument == null) {
            throw new UnknownRow(rowId);
        }

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
            if (searchCriterion.containsEverythingNeededToDefineASearchFilter() == false) {
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

    protected BasicDBList flatten(List<Document> rowDocuments, Purpose purpose) {

        BasicDBList rowsFlat = new BasicDBList();

        for (Document document : rowDocuments) {

            Document row = new Document();

            Document metadata = (Document) document.get(RowDocument.MONGO_KEY__METADATA);

            row.put(Metadata.MONGO_KEY__SUBMISSION_DATE, toString((Date) metadata.get(Metadata.MONGO_KEY__SUBMISSION_DATE)));
            row.put(Metadata.MONGO_KEY__SUBMISSION_DATE, toString((Date) metadata.get(Metadata.MONGO_KEY__SUBMISSION_DATE)));
            row.put(Metadata.MONGO_KEY__SUBMITTER, metadata.get(Metadata.MONGO_KEY__SUBMITTER));
            row.put(Metadata.MONGO_KEY__PROJECT_NAME, metadata.get(Metadata.MONGO_KEY__PROJECT_NAME));
            row.put(Metadata.MONGO_KEY__CHARGE_NUMBER, metadata.get(Metadata.MONGO_KEY__CHARGE_NUMBER));
            row.put(Metadata.MONGO_KEY__COMMENTS, metadata.get(Metadata.MONGO_KEY__COMMENTS));

            row.put(IDatasetDocument.DISPLAY_FIELD__SOURCE_UUID, getObjectId(document, RowDocument.MONGO_KEY__DATASET_ID));

            if (purpose == Purpose.FOR_FILE_DOWNLOAD) {
                // including in case someone might find it helpful to have
                row.put(IRowDocument.DISPLAY_FIELD__ROW_UUID, getObjectId(document, RowDocument.MONGO_KEY__ID));
            }

            Document data = (Document) document.get(RowDocument.MONGO_KEY__DATA);
            String datasetId = getObjectId(document, RowDocument.MONGO_KEY__DATASET_ID);

            Document sourceDocument = (Document) metadata.get(Metadata.MONGO_KEY__SOURCE_DOCUMENT);

            String originalFileName = (String) sourceDocument.get(StoredFile.MONGO_KEY__ORIGINAL_FILE_NAME);
            Integer rowNumber = (Integer) data.get(Row.MONGO_KEY__ROW_NUMBER);

            String nameOfSubdocumentContainingDataIfApplicable = (String)metadata.get(Metadata.MONGO_KEY__SUB_DOCUMENT_NAME);
            boolean thereIsASubdocument = StringUtils.isNotBlank(nameOfSubdocumentContainingDataIfApplicable);

            if (thereIsASubdocument == false) {
                nameOfSubdocumentContainingDataIfApplicable = "N/A";
            }

            if (purpose == Purpose.FOR_FILE_DOWNLOAD) {

                row.put(Metadata.MONGO_KEY__SOURCE_DOCUMENT, originalFileName);
                row.put(Metadata.MONGO_KEY__SUB_DOCUMENT_NAME, nameOfSubdocumentContainingDataIfApplicable);
                row.put(Row.MONGO_KEY__ROW_NUMBER, rowNumber);
                row.put(Metadata.MONGO_KEY__DATA_CATEGORY, metadata.get(Metadata.MONGO_KEY__DATA_CATEGORY));
            }
            else if (purpose == Purpose.FOR_SCREEN_DIAPLAYED_SEARCH_RESULTS) {

                // link for downloading the file
                // DESIGN NOTE: I know, this is the wrong architectural layer. I'm in a time crunch right now.

                row.put(Metadata.MONGO_KEY__SOURCE_DOCUMENT,
                        "<a href='" + ServletContainerConfig.CONTEXT_PATH +
                        "/api/v01/dataset/" + datasetId + "/sourceDocument' " +
                        "target='_blank'>" +
                        originalFileName + "</a>");

                row.put(Metadata.MONGO_KEY__SUB_DOCUMENT_NAME, nameOfSubdocumentContainingDataIfApplicable);
                row.put(Row.MONGO_KEY__ROW_NUMBER, rowNumber);
            }

            Set<String> columnNames = data.keySet();
            columnNames = Utilities.toSortedSet(columnNames);

            for (String name : columnNames) {

                Object value = data.get(name);

                if (value == null) {

                }
                else if (Row.MONGO_KEY__ROW_NUMBER.equals(name)) {
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

                List attachments = (List) metadata.get(Metadata.MONGO_KEY__ATTACHMENTS);
                String originalFileNames = toOriginalFileNames(attachments);

                if (StringUtils.isNotBlank(originalFileNames)) {
                    row.put("Attachments",
                            "<a href='" + ServletContainerConfig.CONTEXT_PATH +
                                    "/api/v01/dataset/" + datasetId + "/attachments' " +
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
            Object originalFilename = document.get(StoredFile.MONGO_KEY__ORIGINAL_FILE_NAME);
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

        XSSFWorkbook workbook = SearchResultsFileWriter_ExcelWorkbook.toExcelWorkbook(basicDBList, query);
        return workbook;
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
                RowDocument.MONGO_KEY__DATASET_ID, new ObjectId(datasetId));

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
