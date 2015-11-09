package gov.energy.nbc.car.businessObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.dto.RowSearchCriteria;
import gov.energy.nbc.car.businessObject.dto.StoredFile;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.dto.MongoFieldNameEncoder;
import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
import gov.energy.nbc.car.model.TestData;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.RowDocument;
import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.*;

import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static org.junit.Assert.assertTrue;


public abstract class AbsDatasetBOTest extends TestUsingTestData
{
    Logger log = Logger.getLogger(getClass());

    @BeforeClass
    public static void beforeClass() {

        TestUsingTestData.beforeClass();

    }

    protected abstract void initializeBusinessObject();

    @AfterClass
    public static void afterClass() {
//        TestUsingTestData.afterClass();
    }

    @Before
    public void before() {
        super.before();
        initializeBusinessObject();
    }

    @After
    public void after() {
        super.after();
    }

    @Test
    public void testGetById() {

        String datasetId = TestData.dataset_1_objectId.toHexString();
        String json = Application.getBusinessObjects().getDatasetBO().getDataset(TestMode.TEST_MODE, datasetId);

        assertTrue(json != null);

        BasicDBObject parsedJson = (BasicDBObject) DAOUtilities.parse(json);
        ObjectId objectId = (ObjectId) parsedJson.get("_id");
        Object id = objectId.toHexString();

        assertTrue(id.equals(TestData.dataset_1_objectId.toString()));
    }

    @Test
    public void testPerformance() {

        if (true) return;
        try {
            StoredFile dataFile = new StoredFile("46RowsAndOver3000Columns.csv", "/46RowsAndOver3000Columns.csv");

            int numberOfDatasetsSeeded = 0;
            int increment = 1;

            for (int AbsDatasetBOTest = 0; AbsDatasetBOTest < 1; AbsDatasetBOTest++) {

                numberOfDatasetsSeeded += increment;
                seedData(dataFile, increment);
                performQueries_homeGrownWay(numberOfDatasetsSeeded);
            }

        } catch (UnsupportedFileExtension unsupportedFileExtension) {
            unsupportedFileExtension.printStackTrace();
        } catch (InvalidValueFoundInHeader invalidValueFoundInHeader) {
            invalidValueFoundInHeader.printStackTrace();
        }
    }

    protected void performQueries(int numberOfDatasetsSeeded) {

        log.info("=============================== " + numberOfDatasetsSeeded + " ===============================");

        PerformanceLogger performanceLogger;

        String metadata = RowDocument.ATTR_KEY__METADATA;
        Bson dataCategoryQuery = eq(metadata + "." + Metadata.ATTR_KEY__SAMPLE_TYPE, "sample type");
        Bson valueQuery = eq(RowDocument.ATTR_KEY__DATA + ".2497" + MongoFieldNameEncoder.DECIMAL_POINT_SUBSTITUTE + "0", 2.0932496);
        Bson bothQueries = and(dataCategoryQuery, valueQuery);

        Bson projection = fields(include(
                RowDocument.ATTR_KEY__ID,
                RowDocument.ATTR_KEY__DATASET_ID,
                metadata + "." + Metadata.ATTR_KEY__SAMPLE_TYPE,
                metadata + "." + Metadata.ATTR_KEY__SUBMISSION_DATE,
                metadata + "." + Metadata.ATTR_KEY__SUBMITTER,
                metadata + "." + Metadata.ATTR_KEY__UPLOADED_FILE));

        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + dataCategoryQuery.toString() + ")", true);
        IBusinessObjects businessObjects = Application.getBusinessObjects();
        String rows = businessObjects.getRowBO().getRows(
                TestMode.TEST_MODE,
                dataCategoryQuery,
                projection);
        performanceLogger.done();
        BasicDBList results = (BasicDBList) DAOUtilities.parse(rows);

        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + valueQuery.toString() + ")", true);
        rows = businessObjects.getRowBO().getRows(
                TestMode.TEST_MODE,
                valueQuery,
                projection);
        performanceLogger.done();
        results = (BasicDBList)DAOUtilities.parse(rows);
        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + bothQueries.toString() + ")", true);
        rows = businessObjects.getRowBO().getRows(
                TestMode.TEST_MODE,
                bothQueries,
                projection);
        performanceLogger.done();
        results = (BasicDBList)DAOUtilities.parse(rows);
    }


    protected void performQueries_homeGrownWay(int numberOfDatasetsSeeded) {

        log.info("=============================== " + numberOfDatasetsSeeded + " ===============================");

        RowSearchCriteria rowSearchCriteria = new RowSearchCriteria();

        rowSearchCriteria.addCriterion_data(
                "FEEDSTOCK",
                "corn stover");
        rowSearchCriteria.addCriterion_data(
                "SCANNING_METHOD",
                "Quarter Cup");
        rowSearchCriteria.addCriterion_data(
                "2497" + MongoFieldNameEncoder.DECIMAL_POINT_SUBSTITUTE + "0",
                0.5887146);

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")", true);

        IBusinessObjects businessObjects = Application.getBusinessObjects();

        String json = businessObjects.getRowBO().getRows(
                TestMode.TEST_MODE,
                rowSearchCriteria);

        performanceLogger.done();

        assertTrue(json != null);

        BasicDBList results = (BasicDBList) DAOUtilities.parse(json);
        int numberOfResults = results.size();
        System.out.println("### results.size(): " + numberOfResults);
        assertTrue(numberOfResults > 0);


        rowSearchCriteria = new RowSearchCriteria();
        rowSearchCriteria.addCriterion_data(
                "FEEDSTOCK",
                "corn stover");

        performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")", true);

        json = businessObjects.getRowBO().getRows(
                TestMode.TEST_MODE,
                rowSearchCriteria);

        performanceLogger.done();

        assertTrue(json != null);

        results = (BasicDBList) DAOUtilities.parse(json);
        numberOfResults = results.size();
        System.out.println("### results.size(): " + numberOfResults);
        assertTrue(numberOfResults > 0);
    }

    protected void seedData(StoredFile dataFile, int number) throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "Seeding " + number + " additional datasets for test.", true);

        IBusinessObjects businessObjects = Application.getBusinessObjects();

        for (int i = 0; i < number; i++) {

            String id = businessObjects.getDatasetBO().addDataset(
                    TestMode.TEST_MODE,
                    "sample type",
                    new Date(),
                    "submitter",
                    "project name",
                    "charge number",
                    "comments",
                    dataFile,
                    "",
                    null);
        }

        performanceLogger.done();
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
