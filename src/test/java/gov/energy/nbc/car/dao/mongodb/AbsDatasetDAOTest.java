package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.bo.ITestDataBO;
import gov.energy.nbc.car.bo.TestData;
import gov.energy.nbc.car.bo.TestMode;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.model.IDataCategoryDocument;
import gov.energy.nbc.car.model.mongodb.document.DatasetDocument;
import org.bson.Document;
import org.junit.*;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;


public abstract class AbsDatasetDAOTest extends TestUsingTestData
{
    private static IDatasetDAO datasetDAO;

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
        super.before();

        initializeBusinessObjects();
        datasetDAO = Application.
                getBusinessObjects().
                getDatasetBO().
                getDatasetDAO(TestMode.TEST_MODE);
    }

    @After
    public void after() {
        super.after();
    }

    @Test
    public void testGetById() {

        DatasetDocument document = datasetDAO.getDataset(TestData.dataset_1_objectId.toHexString());
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(TestData.dataset_1_objectId.toString()));
    }

    @Test
    public void testQueryFilter() {

        Document idFilter = new Document().append("_id", TestData.dataset_1_objectId);
        List<Document> documents = datasetDAO.get(idFilter);
        assertTrue(documents != null);
        assertTrue(documents.size() == 1);
        assertTrue(documents.get(0).get("_id").toString().equals(TestData.dataset_1_objectId.toString()));
    }

    @Test
    public void testQueryForOneFilter_1() {

        Document idFilter = new Document().append("_id", TestData.dataset_1_objectId);
        Document document = datasetDAO.getOne(idFilter);
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(TestData.dataset_1_objectId.toString()));
    }

    @Test
    public void testThatTheDataCategoriesAreBeingSetRight() {

        DataCategoryDAO dataCategoryDAO = datasetDAO.getDataCategoryDAO();

        ITestDataBO testDataBO = Application.getBusinessObjects().getTestDataBO();
        testDataBO.removeTestData();
        testDataBO.seedTestDataInTheDatabase_dataset_1();

        IDataCategoryDocument dataCategoryDocument = dataCategoryDAO.getByName(TestData.ALGEA);
        assertTrue(dataCategoryDocument.getDataCategory().equals(TestData.ALGEA));

        Set<String> columnNames = dataCategoryDocument.getColumnNames();
        assertTrue(columnNames.size() == 8);
        assertTrue(columnNames.contains("_origDocRowNum"));
        assertTrue(columnNames.contains("Some Column Name"));
        assertTrue(columnNames.contains("Boolean Values Column Name"));
        assertTrue(columnNames.contains("String Values Column Name"));
        assertTrue(columnNames.contains("Date Values Column Name"));
        assertTrue(columnNames.contains("Float Values Column Name"));
        assertTrue(columnNames.contains("Integer Values Column Name"));
        assertTrue(columnNames.contains("Varying Value Types Column Name"));

        testDataBO.seedTestDataInTheDatabase_dataset_2();

        dataCategoryDocument = dataCategoryDAO.getByName(TestData.ALGEA);
        assertTrue(dataCategoryDocument.getDataCategory().equals(TestData.ALGEA));

        columnNames = dataCategoryDocument.getColumnNames();
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

        datasetDAO.delete(TestData.dataset_1_objectId);
        datasetDAO.delete(TestData.dataset_2_objectId);

        dataCategoryDocument = dataCategoryDAO.getByName(TestData.ALGEA);
        columnNames = dataCategoryDocument.getColumnNames();
        // None of the data should hve gone away, as datasetDAO.removeAllDataFromCollection() should not
        // cascade to the sample type colletion.
        assertTrue(columnNames.size() == 10);
    }
}
