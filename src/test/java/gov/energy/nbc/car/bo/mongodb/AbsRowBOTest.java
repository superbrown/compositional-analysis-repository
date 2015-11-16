package gov.energy.nbc.car.bo.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.IRowBO;
import gov.energy.nbc.car.bo.TestMode;
import gov.energy.nbc.car.dao.dto.ComparisonOperator;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.mongodb.TestUsingTestData;
import gov.energy.nbc.car.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.junit.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gov.energy.nbc.car.dao.dto.ComparisonOperator.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public abstract class AbsRowBOTest extends TestUsingTestData
{
    Logger log = Logger.getLogger(getClass());

    @BeforeClass
    public static void beforeClass() {
        TestUsingTestData.beforeClass();
    }

    protected abstract void initializeBusinessObjects();

    @AfterClass
    public static void afterClass() {
        TestUsingTestData.afterClass();
    }

    @Before
    public void before() {

        initializeBusinessObjects();

        super.before();

        StoredFile dataFile = new StoredFile("SpreadsheetWithDifferentTypesOfValues.xlsx", "/SpreadsheetWithDifferentTypesOfValues.xlsx");

        try {
            String id = Application.getBusinessObjects().getDatasetBO().addDataset(
                    TestMode.TEST_MODE,
                    "sample type",
                    new Date(),
                    "submitter",
                    "project name",
                    "charge number",
                    "comments",
                    dataFile,
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

        IBusinessObjects businessObjects = Application.getBusinessObjects();

        String columnName = "Number";
        double doubleValue = 3;

        IRowBO rowBO = businessObjects.getRowBO();

        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, EQUALS).size() == 1);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, GREATER_THAN).size() == 2);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, GREATER_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, LESS_THAN).size() == 2);
        assertTrue(exerciseANumbericQuery(rowBO, columnName, doubleValue, LESS_THAN_OR_EQUAL).size() == 3);
    }

    @Test
    public void testQueryCriteria_dates() {

        IBusinessObjects businessObjects = Application.getBusinessObjects();

        String columnName = "Date";
        Date dateValue = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
            dateValue = sdf.parse("1/3/2015");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        IRowBO rowBO = businessObjects.getRowBO();

        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, GREATER_THAN).size() == 2);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, GREATER_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, LESS_THAN).size() == 2);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, LESS_THAN_OR_EQUAL).size() == 3);
        assertTrue(exerciseADateQuery(rowBO, columnName, dateValue, EQUALS).size() == 1);
    }

    @Test
    public void testQueryCriteria_strings() {

        IBusinessObjects businessObjects = Application.getBusinessObjects();

        String columnName = "String";
        String stringValue = "c string";

        IRowBO rowBO = businessObjects.getRowBO();

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

        IBusinessObjects businessObjects = Application.getBusinessObjects();

        String columnName = "Boolean";
        boolean booleanValue = false;

        IRowBO rowBO = businessObjects.getRowBO();

        assertTrue(exerciseABooleanQuery(rowBO, columnName, booleanValue, EQUALS).size() == 2);

        booleanValue = true;
        assertTrue(exerciseABooleanQuery(rowBO, columnName, booleanValue, EQUALS).size() == 3);
    }

    private BasicDBList exerciseABooleanQuery(IRowBO rowBO, String name, boolean value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = rowBO.getRows(TestMode.TEST_MODE, rowSearchCriteria);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }

    private BasicDBList exerciseANumbericQuery(IRowBO rowBO, String name, double value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = rowBO.getRows(TestMode.TEST_MODE, rowSearchCriteria);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }

    private BasicDBList exerciseADateQuery(IRowBO rowBO, String name, Date value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = rowBO.getRows(TestMode.TEST_MODE, rowSearchCriteria);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }

    private BasicDBList exerciseAStringQuery(IRowBO rowBO, String name, String value, ComparisonOperator comparisonOperator) {

        List<SearchCriterion> rowSearchCriteria = new ArrayList ();
        rowSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
        String json = rowBO.getRows(TestMode.TEST_MODE, rowSearchCriteria);
        BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
        return basicDBList;
    }
}
