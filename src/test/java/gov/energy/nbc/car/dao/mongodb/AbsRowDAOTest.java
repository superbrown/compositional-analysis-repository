package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.bo.TestMode;
import gov.energy.nbc.car.dao.dto.RowSearchCriteria;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.bo.mongodb.TestData;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.document.DatasetDocument;
import gov.energy.nbc.car.model.mongodb.document.RowDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.*;

import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static gov.energy.nbc.car.dao.dto.ComparisonOperator.EQUALS;
import static org.junit.Assert.assertTrue;


public abstract class AbsRowDAOTest extends TestUsingTestData
{
    private static IRowDAO rowDAO;

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

        Settings_forUnitTestPurposes settings = new Settings_forUnitTestPurposes();
        initializeBusinessObjects(settings, settings);

        rowDAO = Application.
                getBusinessObjects().
                getRowBO().
                getRowDAO(TestMode.TEST_MODE);
    }

    protected abstract void initializeBusinessObjects(Settings settings, Settings_forUnitTestPurposes settings_forUnitTestPurposes);

    @After
    public void after() {
        super.after();
    }

    @Test
    public void testThatQueriesWorkAsExpectedOnColumnsWithHeterogenousDataTypes() {

        Bson filter = eq(RowDocument.ATTR_KEY__DATA + "." +
                MongoFieldNameEncoder.toMongoSafeFieldName(
                        "Varying Value Types Column Name"),
                TestData.date_1);
        List<Document> results = rowDAO.get(filter);
        assertTrue(results.size() == 1);

        filter = eq(RowDocument.ATTR_KEY__DATA + "." +
                MongoFieldNameEncoder.toMongoSafeFieldName(
                        "Varying Value Types Column Name"),
                "1000");
        results = rowDAO.get(filter);
        assertTrue(results.size() == 1);

        filter = eq(RowDocument.ATTR_KEY__DATA + "." +
                MongoFieldNameEncoder.toMongoSafeFieldName(
                        "Varying Value Types Column Name"),
                1000);
        results = rowDAO.get(filter);
        assertTrue(results.size() == 1);

        filter = eq(RowDocument.ATTR_KEY__DATA + "." +
                MongoFieldNameEncoder.toMongoSafeFieldName(
                        "Varying Value Types Column Name"),
                1000.00);
        results = rowDAO.get(filter);
        assertTrue(results.size() == 1);

        filter = eq(RowDocument.ATTR_KEY__DATA + "." +
                MongoFieldNameEncoder.toMongoSafeFieldName(
                        "Varying Value Types Column Name"),
                1300.54);
        results = rowDAO.get(filter);
        assertTrue(results.size() == 1);

        filter = eq(RowDocument.ATTR_KEY__DATA + "." +
                MongoFieldNameEncoder.toMongoSafeFieldName(
                        "Varying Value Types Column Name")
                , "string value");
        results = rowDAO.get(filter);
        assertTrue(results.size() == 1);
    }

    @Test
    public void testQueryForOneFilter_1() {

        Document idFilter = new Document().append(RowDocument.ATTR_KEY__DATASET_ID, TestData.dataset_1_objectId);
        List<Document> documents = rowDAO.get(idFilter);
        assertTrue(documents != null);
        assertTrue(documents.size() == 5);

        for (Document document : documents) {
            assertTrue(document.get(RowDocument.ATTR_KEY__DATASET_ID).equals(TestData.dataset_1_objectId));
        }
    }

    @Test
    public void testQueryForOneFilter_2() {

        Bson filter = eq(RowDocument.ATTR_KEY__DATA + "." +
                MongoFieldNameEncoder.toMongoSafeFieldName(
                        "String Values Column Name"),
                "String 1");
        List<Document> results = rowDAO.get(filter);
        assertTrue(results.size() == 1);
    }

    @Test
    public void testQueryForOneFilter_3() {

        Bson filter = and(
                eq(DatasetDocument.ATTR_KEY__METADATA + "." + Metadata.ATTR_KEY__SAMPLE_TYPE, TestData.dataCategory),
                eq(RowDocument.ATTR_KEY__DATA + "." +
                        MongoFieldNameEncoder.toMongoSafeFieldName(
                                "Date Values Column Name"),
                        TestData.date_2)
        );

        Document fieldsToInclude = new Document().append(DatasetDocument.ATTR_KEY__METADATA, 1);
        List<Document> results = rowDAO.get(filter);
        assertTrue(results.size() == 2);
    }

    @Test
    public void testThatTheRightNumberOfSpreasheetRowDocumentsExist() {

        int numberOfRowsThatShouldExist =
                (TestData.rowCollection_1.getRows().size()) +
                        (TestData.rowCollection_2.getRows().size());

        long numberOfRowsThatActuallyExist =
                ((IMongodbDAO)rowDAO).getCollection().count();

        assertTrue(numberOfRowsThatActuallyExist == numberOfRowsThatShouldExist);
    }

    @Test
    public void testQuery_rowSearchCriteria() {

        RowSearchCriteria rowSearchCriteria = new RowSearchCriteria();
        rowSearchCriteria.addCriterion_data("Some Column Name", 1, EQUALS);
        rowSearchCriteria.addCriterion_data("Float Values Column Name", 1.22, EQUALS);
        rowSearchCriteria.addCriterion_data("Additional Column Name 1", "a1", EQUALS);

        List<Document> documents = rowDAO.query(rowSearchCriteria);
        assertTrue(documents.size() == 1);

        Document document = documents.get(0);
        Document data = (Document) document.get(RowDocument.ATTR_KEY__DATA);
        assertTrue(((Integer)data.get("Some Column Name")) == 1);
        assertTrue(((Double)data.get("Float Values Column Name")) == 1.22);
        assertTrue(data.get("Additional Column Name 1").equals("a1"));
    }
}
