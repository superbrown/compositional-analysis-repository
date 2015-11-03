package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.businessObject.BusinessObjects;
import gov.energy.nbc.car.model.TestData;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import gov.energy.nbc.car.model.document.SpreadsheetRowDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.*;

import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.Assert.assertTrue;


public class SpreadsheetRowDocumentDAOTest extends TestUsingTestData
{
    private static SpreadsheetRowDocumentDAO spreadsheetRowDocumentDAO;

    @BeforeClass
    public static void beforeClass() {

        TestUsingTestData.beforeClass();
        spreadsheetRowDocumentDAO = new SpreadsheetRowDocumentDAO(BusinessObjects.settings_forUnitTestPurposes);
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
    public void testThatQueriesWorkAsExpectedOnColumnsWithHeterogenousDataTypes() {

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
    public void testQueryForOneFilter_1() {

        Document idFilter = new Document().append(SpreadsheetRowDocument.ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID, TestData.objectId_1);
        List<Document> documents = spreadsheetRowDocumentDAO.query(idFilter);
        assertTrue(documents != null);
        assertTrue(documents.size() == 5);

        for (Document document : documents) {
            assertTrue(document.get(SpreadsheetRowDocument.ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID).equals(TestData.objectId_1));
        }
    }

    @Test
    public void testQueryForOneFilter_2() {

        Bson filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".String Values Column Name", "String 1");
        List<Document> results = spreadsheetRowDocumentDAO.query(filter);
        assertTrue(results.size() == 1);
    }

    @Test
    public void testQueryForOneFilter_3() {

        Bson filter = and(
                eq(SpreadsheetDocument.ATTRIBUTE_KEY__METADATA + "." + Metadata.ATTRIBUTE_KEY__SAMPLE_TYPE, TestData.sampleType),
                eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Date Values Column Name", TestData.date_2)
        );

        Document fieldsToInclude = new Document().append(SpreadsheetDocument.ATTRIBUTE_KEY__METADATA, 1);
        List<Document> results = spreadsheetRowDocumentDAO.query(filter);

        assertTrue(results.size() == 2);
    }

    @Test
    public void testThatTheRightNumberOfSpreasheetRowDocumentsExist() {

        int numberOfSpreadsheetRowsThatShouldExist =
                (TestData.spreadsheetData_1.size()) +
                        (TestData.spreadsheetData_2.size());

        long numberOfSpreadsheetRowsThatActuallyExist =
                spreadsheetRowDocumentDAO.getCollection().count();

        assertTrue(numberOfSpreadsheetRowsThatActuallyExist == numberOfSpreadsheetRowsThatShouldExist);
    }
}
