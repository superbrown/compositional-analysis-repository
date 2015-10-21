package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.businessService.BusinessServices;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import gov.energy.nbc.car.model.document.SpreadsheetRowDocument;
import gov.energy.nbc.car.model.TestData;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.*;

import java.util.List;

import static com.mongodb.client.model.Filters.*;
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
    public void testGetByFilter() {

        Document idFilter = new Document();
        idFilter.put("_id", TestData.objectId_1);
        List<Document> documents = spreadsheetDocumentDAO.query(idFilter);
        assertTrue(documents != null);
        assertTrue(documents.size() == 1);
        assertTrue(documents.get(0).get("_id").toString().equals(TestData.objectId_1.toString()));
    }

    @Test
    public void testGetOneByFilter_1() {

        Document idFilter = new Document();
        idFilter.put("_id", TestData.objectId_1);
        Document document = spreadsheetDocumentDAO.queryForOne(idFilter);
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(TestData.objectId_1.toString()));
    }

    @Test
    public void testGetOneByFilter_2() {

        Bson filter = eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".String Values Column Name", "String 1");
        List<Document> results = spreadsheetDocumentDAO.query(filter);

        assertTrue(results.size() == 1);
    }

    @Test
    public void testGetOneByFilter_3() {

        Bson spreadsheetFilter = and(
                eq(SpreadsheetDocument.ATTRIBUTE_KEY__METADATA + "." + Metadata.ATTRIBUTE_KEY__SAMPLE_TYPE, TestData.sampleType),
                eq(SpreadsheetRowDocument.ATTRIBUTE_KEY__DATA + ".Date Values Column Name", TestData.date_2)
        );

        Document fieldsToInclude = new Document();
        fieldsToInclude.put(SpreadsheetDocument.ATTRIBUTE_KEY__METADATA, 1);
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
}
