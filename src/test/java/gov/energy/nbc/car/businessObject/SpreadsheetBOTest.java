package gov.energy.nbc.car.businessObject;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.businessObject.dto.StoredFile;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.fileReader.InvalidValueFoundInHeader;
import gov.energy.nbc.car.fileReader.UnsupportedFileExtension;
import gov.energy.nbc.car.model.TestData;
import org.bson.types.ObjectId;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertTrue;


public class SpreadsheetBOTest extends TestUsingTestData
{
    private static SpreadsheetBO spreadsheetBO;

    @BeforeClass
    public static void beforeClass() {

        TestUsingTestData.beforeClass();

        Settings_forUnitTestPurposes settings = BusinessObjects.settings_forUnitTestPurposes;
        spreadsheetBO = new SpreadsheetBO(settings, settings);
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

        String spreadsheetId = TestData.objectId_1.toHexString();
        String json = spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, spreadsheetId);

        assertTrue(json != null);

        BasicDBObject parsedJson = (BasicDBObject) DAOUtilities.parse(json);
        ObjectId objectId = (ObjectId) parsedJson.get("_id");
        Object id = objectId.toHexString();

        assertTrue(id.equals(TestData.objectId_1.toString()));
    }

    @Test
    public void testPerformance() {

        try {
            StoredFile dataFile =
                    new StoredFile("46RowsAndOver3000Columns.csv", "/46RowsAndOver3000Columns.csv");

            String id = BusinessObjects.spreadsheetBO.addSpreadsheet(
                    TestMode.TEST_MODE,
                    "sample type",
                    new Date(),
                    "submitter",
                    "project name",
                    "charge number",
                    "comments",
                    dataFile,
                    "",
                    new ArrayList<StoredFile>());

            String spreadsheet = BusinessObjects.spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, id);
            System.out.println(spreadsheet);

            String rowsForSpreadsheet = BusinessObjects.spreadsheetRowBO.getRowsForSpreadsheet(TestMode.TEST_MODE, id);
            System.out.println(rowsForSpreadsheet);

        } catch (UnsupportedFileExtension unsupportedFileExtension) {
            unsupportedFileExtension.printStackTrace();
        } catch (InvalidValueFoundInHeader invalidValueFoundInHeader) {
            invalidValueFoundInHeader.printStackTrace();
        }
    }


//    @Test
//    public void testAddAndGetSpreadsheet() {
//
//        String spreadsheet_1_id = TestData.objectId_1.toHexString();
//
//        String spreadsheet_1_json = spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, spreadsheet_1_id);
//
//        SpreadsheetDocument spreadsheet_1 = new SpreadsheetDocument(spreadsheet_1_json);
//        spreadsheet_1.remove(AbstractDocument.ATTRIBUTE_KEY__ID);
//        spreadsheet_1_json = spreadsheet_1.toJson();
//        String newObjectId = spreadsheetBO.addSpreadsheet(TestMode.TEST_MODE, spreadsheet_1_json);
//
//        // confirm that it's new
//        assertTrue(newObjectId != TestData.objectId_1.toHexString());
//        // confirm that it's all letters and numbers
//        assertTrue(StringUtils.isAlphanumeric(newObjectId));
//
//        String newSpreadsheet_json = spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, newObjectId);
//
//        SpreadsheetDocument newSpreadsheetDocument = new SpreadsheetDocument(newSpreadsheet_json);
//        newSpreadsheetDocument.remove(AbstractDocument.ATTRIBUTE_KEY__ID);
//        // they should be the same
//        assertTrue(newSpreadsheetDocument.equals(spreadsheet_1));
//    }
}
