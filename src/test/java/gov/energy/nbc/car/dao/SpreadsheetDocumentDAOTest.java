package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.businessObject.BusinessObjects;
import gov.energy.nbc.car.model.TestData;
import gov.energy.nbc.car.model.document.SampleTypeDocument;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import org.bson.Document;
import org.junit.*;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;


public class SpreadsheetDocumentDAOTest extends TestUsingTestData
{
    private static SpreadsheetDocumentDAO spreadsheetDocumentDAO;

    @BeforeClass
    public static void beforeClass() {

        TestUsingTestData.beforeClass();
        spreadsheetDocumentDAO = new SpreadsheetDocumentDAO(BusinessObjects.settings_forUnitTestPurposes);
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

        SpreadsheetDocument document = spreadsheetDocumentDAO.getSpreadsheet(TestData.objectId_1.toHexString());
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(TestData.objectId_1.toString()));
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
    public void testThatTheSampleTypesAreBeingSetRight() {

        SampleTypeDocumentDAO sampleTypeDocumentDAO = spreadsheetDocumentDAO.getSampleTypeDocumentDAO();

        testDataBO.removeTestData();
        sampleTypeDocumentDAO.removeAllDataFromCollection();

        testDataBO.seedTestDataInTheDatabase_spreadsheet_1();

        SampleTypeDocument sampleTypeDocument = sampleTypeDocumentDAO.getByName(TestData.ALGEA);
        assertTrue(sampleTypeDocument.getSampleType().equals(TestData.ALGEA));

        Set<String> columnNames = sampleTypeDocument.getColumnNames();
        assertTrue(columnNames.size() == 8);
        assertTrue(columnNames.contains("_origDocRowNum"));
        assertTrue(columnNames.contains("Some Column Name"));
        assertTrue(columnNames.contains("Boolean Values Column Name"));
        assertTrue(columnNames.contains("String Values Column Name"));
        assertTrue(columnNames.contains("Date Values Column Name"));
        assertTrue(columnNames.contains("Float Values Column Name"));
        assertTrue(columnNames.contains("Integer Values Column Name"));
        assertTrue(columnNames.contains("Varying Value Types Column Name"));

        testDataBO.seedTestDataInTheDatabase_spreadsheet_2();

        sampleTypeDocument = sampleTypeDocumentDAO.getByName(TestData.ALGEA);
        assertTrue(sampleTypeDocument.getSampleType().equals(TestData.ALGEA));

        columnNames = sampleTypeDocument.getColumnNames();
        assertTrue(columnNames.size() == 10);
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
        assertTrue(columnNames.size() == 10);
    }
}
