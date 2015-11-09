//package gov.energy.nbc.car.businessObject;
//
//import com.mongodb.BasicDBList;
//import com.mongodb.BasicDBObject;
//import gov.energy.nbc.car.Settings_forUnitTestPurposes;
//import gov.energy.nbc.car.TestUsingTestData;
//import gov.energy.nbc.car.businessObject.dto.RowSearchCriteria;
//import gov.energy.nbc.car.businessObject.dto.StoredFile;
//import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
//import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
//import gov.energy.nbc.car.dao.mongodb.dto.MongoFieldNameEncoder;
//import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
//import gov.energy.nbc.car.model.TestData;
//import gov.energy.nbc.car.model.common.Metadata;
//import gov.energy.nbc.car.model.document.RowDocument;
//import gov.energy.nbc.car.utilities.PerformanceLogger;
//import org.apache.log4j.Logger;
//import org.bson.conversions.Bson;
//import org.bson.types.ObjectId;
//import org.junit.*;
//
//import java.util.Date;
//
//import static com.mongodb.client.model.Filters.and;
//import static com.mongodb.client.model.Filters.eq;
//import static com.mongodb.client.model.Projections.fields;
//import static com.mongodb.client.model.Projections.include;
//import static org.junit.Assert.assertTrue;
//
//
//public class DatasetBOTest extends TestUsingTestData
//{
//    Logger log = Logger.getLogger(getClass());
//
//    private static DatasetBO datasetBO;
//
//    @BeforeClass
//    public static void beforeClass() {
//
//        TestUsingTestData.beforeClass();
//
//        Settings_forUnitTestPurposes settings = BusinessObjects.settings_forUnitTestPurposes;
//        datasetBO = new DatasetBO(settings, settings);
//    }
//
//    @AfterClass
//    public static void afterClass() {
//        TestUsingTestData.afterClass();
//    }
//
//    @Before
//    public void before() {
//        super.before();
//    }
//
//    @After
//    public void after() {
//        super.after();
//    }
//
//    @Test
//    public void testGetById() {
//
//        String datasetId = TestData.dataset_1_objectId.toHexString();
//        String json = datasetBO.getDataset(TestMode.TEST_MODE, datasetId);
//
//        assertTrue(json != null);
//
//        BasicDBObject parsedJson = (BasicDBObject) DAOUtilities.parse(json);
//        ObjectId objectId = (ObjectId) parsedJson.get("_id");
//        Object id = objectId.toHexString();
//
//        assertTrue(id.equals(TestData.dataset_1_objectId.toString()));
//    }
//
//    @Test
//    public void testPerformance() {
//
//        try {
//            StoredFile dataFile = new StoredFile("46RowsAndOver3000Columns.csv", "/46RowsAndOver3000Columns.csv");
//
//            int numberOfDatasetsSeeded = 0;
//            numberOfDatasetsSeeded += 1;
//
//            seedData(dataFile, numberOfDatasetsSeeded);
//            performQueries_homeGrownWay(numberOfDatasetsSeeded);
//
//            numberOfDatasetsSeeded += 10;
//            seedData(dataFile, numberOfDatasetsSeeded);
//            performQueries_homeGrownWay(numberOfDatasetsSeeded);
//
//            numberOfDatasetsSeeded += 100;
//            seedData(dataFile, numberOfDatasetsSeeded);
//            performQueries_homeGrownWay(numberOfDatasetsSeeded);
//
//            numberOfDatasetsSeeded += 1000;
//            seedData(dataFile, numberOfDatasetsSeeded);
//            performQueries_homeGrownWay(numberOfDatasetsSeeded);
//
//
//        } catch (UnsupportedFileExtension unsupportedFileExtension) {
//            unsupportedFileExtension.printStackTrace();
//        } catch (InvalidValueFoundInHeader invalidValueFoundInHeader) {
//            invalidValueFoundInHeader.printStackTrace();
//        }
//    }
//
//    protected void performQueries(int numberOfDatasetsSeeded) {
//
//        log.info("=============================== " + numberOfDatasetsSeeded + " ===============================");
//
//        PerformanceLogger performanceLogger;
//
//        String metadata = RowDocument.ATTR_KEY__METADATA;
//        Bson dataCategoryQuery = eq(metadata + "." + Metadata.ATTR_KEY__SAMPLE_TYPE, "sample type");
//        Bson valueQuery = eq(RowDocument.ATTR_KEY__DATA + ".2497" + MongoFieldNameEncoder.DECIMAL_POINT_SUBSTITUTE + "0", 2.0932496);
//        Bson bothQueries = and(dataCategoryQuery, valueQuery);
//
//        Bson projection = fields(include(
//                RowDocument.ATTR_KEY__ID,
//                RowDocument.ATTR_KEY__DATASET_ID,
//                metadata + "." + Metadata.ATTR_KEY__SAMPLE_TYPE,
//                metadata + "." + Metadata.ATTR_KEY__SUBMISSION_DATE,
//                metadata + "." + Metadata.ATTR_KEY__SUBMITTER,
//                metadata + "." + Metadata.ATTR_KEY__UPLOADED_FILE));
//
//        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + dataCategoryQuery.toString() + ")", true);
//        String rows = BusinessObjects.rowBO.getRows(
//                TestMode.TEST_MODE,
//                dataCategoryQuery,
//                projection);
//        performanceLogger.done();
//        BasicDBList results = (BasicDBList) DAOUtilities.parse(rows);
//
//        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + valueQuery.toString() + ")", true);
//        rows = BusinessObjects.rowBO.getRows(
//                TestMode.TEST_MODE,
//                valueQuery,
//                projection);
//        performanceLogger.done();
//        results = (BasicDBList)DAOUtilities.parse(rows);
//        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + bothQueries.toString() + ")", true);
//        rows = BusinessObjects.rowBO.getRows(
//                TestMode.TEST_MODE,
//                bothQueries,
//                projection);
//        performanceLogger.done();
//        results = (BasicDBList)DAOUtilities.parse(rows);
//    }
//
//
//    protected void performQueries_homeGrownWay(int numberOfDatasetsSeeded) {
//
//        log.info("=============================== " + numberOfDatasetsSeeded + " ===============================");
//
//        RowSearchCriteria rowSearchCriteria = new RowSearchCriteria();
//
//        rowSearchCriteria.addCriterion_data(
//                "FEEDSTOCK",
//                "corn stover");
//        rowSearchCriteria.addCriterion_data(
//                "SCANNING_METHOD",
//                "Quarter Cup");
//        rowSearchCriteria.addCriterion_data(
//                "2497" + MongoFieldNameEncoder.DECIMAL_POINT_SUBSTITUTE + "0",
//                0.5887146);
//
//        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")", true);
//
//        String rows = BusinessObjects.rowBO.getRows(
//                TestMode.TEST_MODE,
//                rowSearchCriteria);
//
//        performanceLogger.done();
//
//        assertTrue(rows != null);
//  //      assertTrue(rows.length() == 1);
//
//        BasicDBList results = (BasicDBList) DAOUtilities.parse(rows);
//        System.out.println("### results.size(): " + results.size());
//
//
//        rowSearchCriteria = new RowSearchCriteria();
//        rowSearchCriteria.addCriterion_data(
//                "FEEDSTOCK",
//                "corn stover");
//
//        performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")", true);
//
//        rows = BusinessObjects.rowBO.getRows(
//                TestMode.TEST_MODE,
//                rowSearchCriteria);
//
//        performanceLogger.done();
//
//        assertTrue(rows != null);
////        assertTrue(rows.length() == 1);
//
//        results = (BasicDBList) DAOUtilities.parse(rows);
//        System.out.println("### results.size(): " + results.size());
//    }
//
//    protected void seedData(StoredFile dataFile, int number) throws UnsupportedFileExtension, InvalidValueFoundInHeader {
//
//        PerformanceLogger performanceLogger = new PerformanceLogger(log, "Seeding " + number + " additional datasets for test.", true);
//
//        for (int i = 0; i < number; i++) {
//
//            String id = BusinessObjects.datasetBO.addDataset(
//                    TestMode.TEST_MODE,
//                    "sample type",
//                    new Date(),
//                    "submitter",
//                    "project name",
//                    "charge number",
//                    "comments",
//                    dataFile,
//                    "",
//                    null);
//        }
//
//        performanceLogger.done();
//    }
//
//
////    @Test
////    public void testAddAndGetDataset() {
////
////        String dataset_1_id = TestData.objectId_1.toHexString();
////
////        String dataset_1_json = datasetBO.getDataset(TestMode.TEST_MODE, dataset_1_id);
////
////        Dataset dataset_1 = new Dataset(dataset_1_json);
////        dataset_1.remove(AbstractDocument.ATTR_KEY__ID);
////        dataset_1_json = dataset_1.toJson();
////        String newObjectId = datasetBO.addDataset(TestMode.TEST_MODE, dataset_1_json);
////
////        // confirm that it's new
////        assertTrue(newObjectId != TestData.objectId_1.toHexString());
////        // confirm that it's all letters and numbers
////        assertTrue(StringUtils.isAlphanumeric(newObjectId));
////
////        String newDataset_json = datasetBO.getDataset(TestMode.TEST_MODE, newObjectId);
////
////        Dataset newDataset = new Dataset(newDataset_json);
////        newDataset.remove(AbstractDocument.ATTR_KEY__ID);
////        // they should be the same
////        assertTrue(newDataset.equals(dataset_1));
////    }
//}
