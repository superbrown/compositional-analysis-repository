package gov.energy.nbc.car.businessObject;

import com.mongodb.BasicDBList;
import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.businessObject.dto.RowSearchCriteria;
import gov.energy.nbc.car.businessObject.dto.StoredFile;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.RowDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.conversions.Bson;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static gov.energy.nbc.car.businessObject.dto.ComparisonOperator.EQUALS;
import static gov.energy.nbc.car.businessObject.dto.ComparisonOperator.LIKE;
import static org.junit.Assert.assertTrue;


public abstract class AbsPerformanceTest extends TestUsingTestData
{
    Logger log = Logger.getLogger(getClass());

    @BeforeClass
    public static void beforeClass() {
        TestUsingTestData.beforeClass();
    }

    protected abstract void initializeBusinessObject();

    @AfterClass
    public static void afterClass() {
        TestUsingTestData.afterClass();
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

//    @Test
    public void testPerformance() {

        try {
            StoredFile dataFile = new StoredFile("46RowsAndOver3000Columns.csv", "/46RowsAndOver3000Columns.csv");

            if (SUSPEND_DATA_SEEDING == true) {

                performQueries_homeGrownWay(-1);
            }
            else {

                int numberOfDatasetsSeeded = 0;
                int increment = 1;

                for (int AbsDatasetBOTest = 0; AbsDatasetBOTest < 1; AbsDatasetBOTest++) {

                    numberOfDatasetsSeeded += increment;
                    seedData(dataFile, increment);
                    performQueries_homeGrownWay(numberOfDatasetsSeeded);
                }
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
        IRowBO rowBO = businessObjects.getRowBO();

        String rows = rowBO.getRows(
                TestMode.TEST_MODE,
                dataCategoryQuery,
                projection);
        performanceLogger.done();
        BasicDBList results = (BasicDBList) DAOUtilities.parse(rows);

        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + valueQuery.toString() + ")", true);
        rows = rowBO.getRows(
                TestMode.TEST_MODE,
                valueQuery,
                projection);
        performanceLogger.done();
        results = (BasicDBList)DAOUtilities.parse(rows);
        performanceLogger = new PerformanceLogger(log, "[performQueries()] rowBO.getRows(" + bothQueries.toString() + ")", true);
        rows = rowBO.getRows(
                TestMode.TEST_MODE,
                bothQueries,
                projection);
        performanceLogger.done();
        results = (BasicDBList)DAOUtilities.parse(rows);
    }


    protected void performQueries_homeGrownWay(int numberOfDatasetsSeeded) {

        log.info("=============================== " + numberOfDatasetsSeeded + " ===============================");

        RowSearchCriteria rowSearchCriteria = new RowSearchCriteria();

        // Test drive all the operators

        rowSearchCriteria.addCriterion_data(
                "FEEDSTOCK",
                "corn stover",
                EQUALS);

        rowSearchCriteria.addCriterion_data(
                "SCANNING_METHOD",
                "Quarter",
                LIKE);

        rowSearchCriteria.addCriterion_data(
                "2497" + MongoFieldNameEncoder.DECIMAL_POINT_SUBSTITUTE + "0",
                0.5887146,
                EQUALS);

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")", true);

        IRowBO rowBO = Application.getBusinessObjects().getRowBO();

        String json = rowBO.getRows(
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
                "corn stover",
                EQUALS);

        performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")", true);

        json = rowBO.getRows(
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
}
