package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.BasicDBObject;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.TestUsingTestData;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.*;

import static org.junit.Assert.assertTrue;


public abstract class AbsDatasetBOTest extends TestUsingTestData
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
    }

    @After
    public void after() {
        super.after();
    }

    @Test
    public void testGetById() {

        String datasetId = TestData.dataset_1_objectId.toHexString();
        String json = getBusinessObjects().getDatasetBO().getDataset(datasetId);

        assertTrue(json != null);

        BasicDBObject parsedJson = (BasicDBObject) DAOUtilities.parse(json);
        ObjectId objectId = (ObjectId) parsedJson.get("_id");
        Object id = objectId.toHexString();

        assertTrue(id.equals(TestData.dataset_1_objectId.toString()));
    }


//    @Test
//    public void testAddAndGetDataset() {
//
//        String dataset_1_id = TestData.objectId_1.toHexString();
//
//        String dataset_1_json = getDatasetBO().getDataset(TestMode.TEST_MODE, dataset_1_id);
//
//        Dataset dataset_1 = new Dataset(dataset_1_json);
//        dataset_1.remove(AbstractDocument.ATTR_KEY__ID);
//        dataset_1_json = dataset_1.toJson();
//        String newObjectId = getDatasetBO().addDataset(TestMode.TEST_MODE, dataset_1_json);
//
//        // confirm that it's new
//        assertTrue(newObjectId != TestData.objectId_1.toHexString());
//        // confirm that it's all letters and numbers
//        assertTrue(StringUtils.isAlphanumeric(newObjectId));
//
//        String newDataset_json = getDatasetBO().getDataset(TestMode.TEST_MODE, newObjectId);
//
//        Dataset newDataset = new Dataset(newDataset_json);
//        newDataset.remove(AbstractDocument.ATTR_KEY__ID);
//        // they should be the same
//        assertTrue(newDataset.equals(dataset_1));
//    }
}
