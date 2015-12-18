package gov.energy.nbc.car.bo.mongodb;

import com.mongodb.BasicDBList;
import gov.energy.nbc.car.ResultsMode;
import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.IRowBO;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.dao.mongodb.TestUsingTestData;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import gov.energy.nbc.car.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gov.energy.nbc.car.dao.dto.ComparisonOperator.CONTAINS;
import static gov.energy.nbc.car.dao.dto.ComparisonOperator.EQUALS;
import static org.junit.Assert.assertTrue;


public abstract class AbsPerformanceTest extends TestUsingTestData
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
    public void testPerformance() {

        try {
            StoredFile dataFile = new StoredFile("46RowsAndOver3000Columns.csv", "/46RowsAndOver3000Columns.csv");

            if (SUSPEND_DATA_SEEDING == true) {

                performQueries_homeGrownWay(-1);
            }
            else {

                int numberOfDatasetsSeeded = 0;
                int increment = 1;

                for (int i = 0; i < 1; i++) {

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

    protected void performQueries_homeGrownWay(int numberOfDatasetsSeeded) {

        log.info("=============================== " + numberOfDatasetsSeeded + " ===============================");

        List<SearchCriterion> rowSearchCriteria = new ArrayList ();

        // Test drive all the operators

        rowSearchCriteria.add(new SearchCriterion(
                "FEEDSTOCK",
                "corn stover",
                EQUALS));

        rowSearchCriteria.add(new SearchCriterion(
                "SCANNING_METHOD",
                "Quarter",
                CONTAINS));

        rowSearchCriteria.add(new SearchCriterion(
                "2497" + MongoFieldNameEncoder.DECIMAL_POINT_SUBSTITUTE + "0",
                0.5887146,
                EQUALS));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")");

        IRowBO rowBO = getBusinessObjects().getRowBO();
        String json = rowBO.getRows(rowSearchCriteria, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);

        performanceLogger.done();

        assertTrue(json != null);

        BasicDBList results = (BasicDBList) DAOUtilities.parse(json);
        int numberOfResults = results.size();
        System.out.println("### results.size(): " + numberOfResults);
        assertTrue(numberOfResults > 0);


        rowSearchCriteria = new ArrayList();
        rowSearchCriteria.add(new SearchCriterion(
                "FEEDSTOCK",
                "corn stover",
                EQUALS));

        performanceLogger = new PerformanceLogger(log, "[performQueries_homeGrownWay()] rowBO.getRows(" + rowSearchCriteria.toString() + ")");

        json = rowBO.getRows(rowSearchCriteria, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);

        performanceLogger.done();

        assertTrue(json != null);

        results = (BasicDBList) DAOUtilities.parse(json);
        numberOfResults = results.size();
        System.out.println("### results.size(): " + numberOfResults);
        assertTrue(numberOfResults > 0);
    }

    protected void seedData(StoredFile dataFile, int number) throws UnsupportedFileExtension, InvalidValueFoundInHeader {

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "Seeding " + number + " additional datasets for test.");

        IDatasetBO datasetBO = getBusinessObjects().getDatasetBO();

        for (int i = 0; i < number; i++) {

            ObjectId objectId = datasetBO.addDataset(
                    TestUsingTestData.DEFAULT_SET_OF_DATA_CATEGORIES[0],
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
