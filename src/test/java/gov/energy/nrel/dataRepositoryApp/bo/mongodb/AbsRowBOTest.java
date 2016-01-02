package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.bo.IRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator;
import gov.energy.nrel.dataRepositoryApp.dao.dto.SearchCriterion;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.TestUsingTestData;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public abstract class AbsRowBOTest extends TestUsingTestData
{
    Logger log = Logger.getLogger(getClass());

    @BeforeClass
    public static void beforeClass() {
        TestUsingTestData.beforeClass();
    }

    @AfterClass
    public static void afterClass() {
        TestUsingTestData.afterClass();
    }

    @Before
    public void before() {

        super.before();

        StoredFile sourceDocument = new StoredFile("SpreadsheetWithDifferentTypesOfValues.xlsx", "/SpreadsheetWithDifferentTypesOfValues.xlsx");

        try {
            ObjectId objectId = getBusinessObjects().getDatasetBO().addDataset(
                    "sample type",
                    new Date(),
                    "submitter",
                    "project name",
                    "charge number",
                    "comments",
                    sourceDocument,
                    "only spreadheet in workbook",
                    null);
        } catch (UnsupportedFileExtension e) {
            e.printStackTrace();
            fail();
        } catch (InvalidValueFoundInHeader e) {
            e.printStackTrace();
            fail();
        }
    }

    @After
    public void after() {
        super.after();
    }

    @Test
    public void testQueryCriteria_numbers() {

        String columnName = "Number";
        double doubleValue = 3;

        IRowBO rowBO = getBusinessObjects().getRowBO();

        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, EQUALS).size() == 1);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, GREATER_THAN).size() == 2);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, GREATER_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, LESS_THAN).size() == 2);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, LESS_THAN_OR_EQUAL).size() == 3);
    }

    @Test
    public void testQueryCriteria_dates() {

        String columnName = "Date";
        Date dateValue = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
            dateValue = sdf.parse("1/3/2015");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        IRowBO rowBO = getBusinessObjects().getRowBO();

        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, GREATER_THAN).size() == 2);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, GREATER_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, LESS_THAN).size() == 2);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, LESS_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, EQUALS).size() == 1);
    }

    @Test
    public void testQueryCriteria_strings() {

        String columnName = "String";
        String stringValue = "c string";

        IRowBO rowBO = getBusinessObjects().getRowBO();

        assertTrue(exerciseAStringQuery(rowBO, columnName, stringValue, EQUALS).size() == 1);
        assertTrue(exerciseAStringQuery(rowBO, columnName, stringValue, GREATER_THAN).size() == 2);
        assertTrue(exerciseAStringQuery(rowBO, columnName, stringValue, GREATER_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseAStringQuery(rowBO, columnName, stringValue, LESS_THAN).size() == 2);
        assertTrue(exerciseAStringQuery(rowBO, columnName, stringValue, LESS_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseAStringQuery(rowBO, columnName, stringValue, CONTAINS).size() == 1);

        assertTrue(exerciseAStringQuery(rowBO, columnName, "string", CONTAINS).size() == 5);
        assertTrue(exerciseAStringQuery(rowBO, columnName, "c str", CONTAINS).size() == 1);
    }

    @Test
    public void testQueryCriteria_booleans() {

        String columnName = "Boolean";
        boolean booleanValue = false;

        IRowBO rowBO = getBusinessObjects().getRowBO();

        assertTrue(exerciseABooleanQuery(rowBO, columnName, booleanValue, EQUALS).size() == 2);

        booleanValue = true;
        assertTrue(exerciseABooleanQuery(rowBO, columnName, booleanValue, EQUALS).size() == 3);
    }

    private BasicDBList exerciseABooleanQuery(IRowBO rowBO, String name, boolean value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = getBusinessObjects().getRowBO().getRows(rowSearchCriteria, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }

    private BasicDBList exerciseANumbericQuery(IRowBO rowBO, String name, double value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = rowBO.getRows(rowSearchCriteria, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }

    private BasicDBList exerciseADateQuery(IRowBO rowBO, String name, Date value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = rowBO.getRows(rowSearchCriteria, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }

    private BasicDBList exerciseAStringQuery(IRowBO rowBO, String name, String value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList ();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = rowBO.getRows(rowSearchCriteria, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }
}
