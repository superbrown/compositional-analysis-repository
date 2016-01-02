package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.BasicDBList;
import gov.energy.nrel.dataRepositoryApp.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.SearchCriterion;
import gov.energy.nrel.dataRepositoryApp.dao.dto.StoredFile;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.TestUsingTestData;
import gov.energy.nrel.dataRepositoryApp.utilities.PerformanceLogger;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.InvalidValueFoundInHeader;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator.CONTAINS;
import static gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator.EQUALS;
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
            StoredFile sourceDocument = new StoredFile("46RowsAndOver3000Columns.csv", "/46RowsAndOver3000Columns.csv");

            if (SUSPEND_DATA_SEEDING == true) {

                performQueries_homeGrownWay(-1);
            }
            else {

                int numberOfDatasetsSeeded = 0;
                int increment = 1;

                for (int i = 0; i < 1; i++) {

                    numberOfDatasetsSeeded += increment;
                    seedData(sourceDocument, increment);
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

    protected void seedData(StoredFile sourceDocument, int number) throws UnsupportedFileExtension, InvalidValueFoundInHeader {

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
                    sourceDocument,
                    "",
                    null);
        }

        performanceLogger.done();
    }
}
