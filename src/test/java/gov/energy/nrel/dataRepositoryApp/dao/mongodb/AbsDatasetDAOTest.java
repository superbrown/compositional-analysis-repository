package gov.energy.nrel.dataRepositoryApp.dao.mongodb;

import gov.energy.nrel.dataRepositoryApp.bo.ITestDataBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.TestData;
import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Row;
import gov.energy.nrel.dataRepositoryApp.model.document.IDataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import org.bson.Document;
import org.junit.*;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public abstract class AbsDatasetDAOTest extends TestUsingTestData
{
    private static IDatasetDAO datasetDAO;

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

        datasetDAO = getBusinessObjects().getDatasetBO().getDatasetDAO();
    }

    @After
    public void after() {
        super.after();
    }

    @Test
    public void testGetById() {

        IDatasetDocument document = null;
        try {
            document = datasetDAO.getDataset(TestData.dataset_1_objectId.toHexString());
        } catch (UnknownDataset e) {
            fail();
        }
        assertTrue(document != null);
        assertTrue(document.getId().equals(TestData.dataset_1_objectId.toString()));
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

        try {
            IDataCategoryDAO dataCategoryDAO = datasetDAO.getDataCategoryDAO();

            ITestDataBO testDataBO = getBusinessObjects().getTestDataBO();
            if (SUSPEND_DATA_CLEANUP == false) {
                testDataBO.removeTestData();
            }
            testDataBO.seedTestDataInTheDatabase_dataset_1();

            IDataCategoryDocument dataCategoryDocument = dataCategoryDAO.getByName(TestData.ALGEA);
            assertTrue(dataCategoryDocument.getName().equals(TestData.ALGEA));

            Set<String> columnNames = dataCategoryDocument.getColumnNames();
            assertTrue(columnNames.size() == 8);
            assertTrue(columnNames.contains(Row.MONGO_KEY__ROW_NUMBER));
            assertTrue(columnNames.contains("Some Column Name"));
            assertTrue(columnNames.contains("Boolean Values Column Name"));
            assertTrue(columnNames.contains("String Values Column Name"));
            assertTrue(columnNames.contains("Date Values Column Name"));
            assertTrue(columnNames.contains("Float Values Column Name"));
            assertTrue(columnNames.contains("Integer Values Column Name"));
            assertTrue(columnNames.contains("Varying Value Types Column Name"));

            testDataBO.seedTestDataInTheDatabase_dataset_2();

            dataCategoryDocument = dataCategoryDAO.getByName(TestData.ALGEA);
            assertTrue(dataCategoryDocument.getName().equals(TestData.ALGEA));

            columnNames = dataCategoryDocument.getColumnNames();
            assertTrue(columnNames.size() == 10);
            assertTrue(columnNames.contains(Row.MONGO_KEY__ROW_NUMBER));
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

            // Check that delete works as we'd expect
            assertTrue(datasetDAO.getCount() == 2);
            assertTrue(datasetDAO.getRowDAO().getCount() == 9);
            assertTrue(datasetDAO.getDataCategoryDAO().getCount() == 1);

            datasetDAO.delete(TestData.dataset_1_objectId);

            assertTrue(datasetDAO.getCount() == 1);
            assertTrue(datasetDAO.getRowDAO().getCount() == 4);
            assertTrue(datasetDAO.getDataCategoryDAO().getCount() == 1);

            datasetDAO.delete(TestData.dataset_2_objectId);

            assertTrue(datasetDAO.getCount() == 0);
            assertTrue(datasetDAO.getRowDAO().getCount() == 0);
            assertTrue(datasetDAO.getDataCategoryDAO().getCount() == 1);

            dataCategoryDocument = dataCategoryDAO.getByName(TestData.ALGEA);
            columnNames = dataCategoryDocument.getColumnNames();
            // None of the data should hve gone away, as datasetDAO.removeAllDataFromCollection() should not
            // cascade to the sample type colletion.
            assertTrue(columnNames.size() == 10);
        }
        catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
}
