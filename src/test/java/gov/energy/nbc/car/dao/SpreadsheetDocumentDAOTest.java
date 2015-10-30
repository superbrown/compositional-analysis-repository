package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.businessService.BusinessServices;
import gov.energy.nbc.car.model.TestData;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.SampleTypeDocument;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import gov.energy.nbc.car.model.document.SpreadsheetRowDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.*;

import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.Assert.assertTrue;


public class SpreadsheetDocumentDAOTest extends TestUsingTestData
{
    private static SpreadsheetDocumentDAO spreadsheetDocumentDAO;

    @BeforeClass
    public static void beforeClass() {

        TestUsingTestData.beforeClass();
        spreadsheetDocumentDAO = new SpreadsheetDocumentDAO(BusinessServices.settings_forUnitTestPurposes);
    }

    @AfterClass
    public static void afterClass() {
        TestUsingTestData.afterClass();
    }

    @Before
    public void before() {
        super.before();
    }

    @After
    public void after() {
        super.after();
    }

    @Test
    public void testGetById() {

        SpreadsheetDocument document = spreadsheetDocumentDAO.get(TestData.objectId_1);
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(TestData.objectId_1.toString()));
    }

    @Test
    public void testThatQueriesWorkAsExpectedOnColumnsWithHeterogenousDataTypes() {

        SpreadsheetRowDocumentDAO spreadsheetRowDocumentDAO = spreadsheetDocumentDAO.getSpreadsheetRowDocumentDAO();

        Bson filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Varying Value Types Column Name", TestData.date_1);
        List<Document> results = spreadsheetRowDocumentDAO.query(filter);
        assertTrue(results.size() == 1);

        filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Varying Value Types Column Name", "1000");
        results = spreadsheetRowDocumentDAO.query(filter);
        assertTrue(results.size() == 1);

        filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Varying Value Types Column Name", 1000);
        results = spreadsheetRowDocumentDAO.query(filter);
        assertTrue(results.size() == 1);

        filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Varying Value Types Column Name", 1000.00);
        results = spreadsheetRowDocumentDAO.query(filter);
        assertTrue(results.size() == 1);

        filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Varying Value Types Column Name", 1300.54);
        results = spreadsheetRowDocumentDAO.query(filter);
        assertTrue(results.size() == 1);

        filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Varying Value Types Column Name", "string value");
        results = spreadsheetRowDocumentDAO.query(filter);
        assertTrue(results.size() == 1);
    }

    @Test
    public void testQueryFilter() {

        Document idFilter = new Document().append("_id", TestData.objectId_1);
        List<Document> documents = spreadsheetDocumentDAO.query(idFilter);
        assertTrue(documents != null);
        assertTrue(documents.size() == 1);
        assertTrue(documents.get(0).get("_id").toString().equals(TestData.objectId_1.toString()));
    }

    @Test
    public void testQueryForOneFilter_1() {

        Document idFilter = new Document().append("_id", TestData.objectId_1);
        Document document = spreadsheetDocumentDAO.queryForOne(idFilter);
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(TestData.objectId_1.toString()));
    }

    @Test
    public void testQueryForOneFilter_2() {

        Bson filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".String Values Column Name", "String 1");
        List<Document> results = spreadsheetDocumentDAO.query(filter);
        assertTrue(results.size() == 1);
    }

    @Test
    public void testQueryForOneFilter_3() {

        Bson spreadsheetFilter = and(
                eq(SpreadsheetDocument.ATTRIBUTE_KEY__METADATA + "." + Metadata.ATTRIBUTE_KEY__SAMPLE_TYPE, TestData.sampleType),
                eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Date Values Column Name", TestData.date_2)
        );

        Document fieldsToInclude = new Document().append(SpreadsheetDocument.ATTRIBUTE_KEY__METADATA, 1);
        List<Document> results = spreadsheetDocumentDAO.query(spreadsheetFilter, fieldsToInclude);
        assertTrue(results.size() == 2);
    }

    @Test
    public void testThatTheRightNumberOfSpreasheetRowDocumentsExist() {

        int numberOfSpreadsheetRowsThatShouldExist =
                (TestData.spreadsheetValues_1.length - 1) +
                        (TestData.spreadsheetValues_2.length - 1);

        long numberOfSpreadsheetRowsThatActuallyExist =
                spreadsheetDocumentDAO.getSpreadsheetRowDocumentDAO().getCollection().count();

        assertTrue(numberOfSpreadsheetRowsThatActuallyExist == numberOfSpreadsheetRowsThatShouldExist);
    }

    @Test
    public void testThatTheSampleTypesAreBeingSetRight() {

        SampleTypeDocumentDAO sampleTypeDocumentDAO = spreadsheetDocumentDAO.getSampleTypeDocumentDAO();

        testDataService.removeTestData();
        sampleTypeDocumentDAO.removeAllDataFromCollection();

        testDataService.seedTestDataInTheDatabase_spreadsheet_1();

        SampleTypeDocument sampleTypeDocument = sampleTypeDocumentDAO.getByName(TestData.ALGEA);
        assertTrue(sampleTypeDocument.getSampleType().equals(TestData.ALGEA));

        Set<String> columnNames = sampleTypeDocument.getColumnNames();
        assertTrue(columnNames.size() == 7);
        assertTrue(columnNames.contains("_origDocRowNum"));
        assertTrue(columnNames.contains("Some Column Name"));
        assertTrue(columnNames.contains("String Values Column Name"));
        assertTrue(columnNames.contains("Date Values Column Name"));
        assertTrue(columnNames.contains("Float Values Column Name"));
        assertTrue(columnNames.contains("Integer Values Column Name"));
        assertTrue(columnNames.contains("Varying Value Types Column Name"));

        testDataService.seedTestDataInTheDatabase_spreadsheet_2();

        sampleTypeDocument = sampleTypeDocumentDAO.getByName(TestData.ALGEA);
        assertTrue(sampleTypeDocument.getSampleType().equals(TestData.ALGEA));

        columnNames = sampleTypeDocument.getColumnNames();
        assertTrue(columnNames.size() == 9);
        assertTrue(columnNames.contains("_origDocRowNum"));
        assertTrue(columnNames.contains("Some Column Name"));
        assertTrue(columnNames.contains("String Values Column Name"));
        assertTrue(columnNames.contains("Date Values Column Name"));
        assertTrue(columnNames.contains("Float Values Column Name"));
        assertTrue(columnNames.contains("Integer Values Column Name"));
        assertTrue(columnNames.contains("Varying Value Types Column Name"));
        assertTrue(columnNames.contains("Varying Value Types Column Name"));
        assertTrue(columnNames.contains("Varying Value Types Column Name"));
        assertTrue(columnNames.contains("Additional Column Name 1"));
        assertTrue(columnNames.contains("Additional new Column Name 2"));

        spreadsheetDocumentDAO.removeAllDataFromCollection();

        sampleTypeDocument = sampleTypeDocumentDAO.getByName(TestData.ALGEA);
        columnNames = sampleTypeDocument.getColumnNames();
        // None of the data should hve gone away, as spreadsheetDocumentDAO.removeAllDataFromCollection() should not
        // cascade to the sample type colletion.
        assertTrue(columnNames.size() == 9);
    }
}
