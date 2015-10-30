package gov.energy.nbc.car.businessObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.TestUsingTestData;
import gov.energy.nbc.car.model.AbstractDocument;
import gov.energy.nbc.car.model.TestData;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.junit.*;

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

        BasicDBObject parsedJson = (BasicDBObject) JSON.parse(json);
        ObjectId objectId = (ObjectId) parsedJson.get("_id");
        Object id = objectId.toHexString();

        assertTrue(id.equals(TestData.objectId_1.toString()));
    }

    @Test
    public void testAddAndGetSpreadsheet() {

        String spreadsheet_1_id = TestData.objectId_1.toHexString();

        String spreadsheet_1_json = spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, spreadsheet_1_id);

        SpreadsheetDocument spreadsheet_1 = new SpreadsheetDocument(spreadsheet_1_json);
        spreadsheet_1.remove(AbstractDocument.ATTRIBUTE_KEY__ID);
        spreadsheet_1_json = spreadsheet_1.toJson();
        String newObjectId = spreadsheetBO.addSpreadsheet(TestMode.TEST_MODE, spreadsheet_1_json);

        // confirm that it's new
        assertTrue(newObjectId != TestData.objectId_1.toHexString());
        // confirm that it's all letters and numbers
        assertTrue(StringUtils.isAlphanumeric(newObjectId));

        String newSpreadsheet_json = spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, newObjectId);

        SpreadsheetDocument newSpreadsheetDocument = new SpreadsheetDocument(newSpreadsheet_json);
        newSpreadsheetDocument.remove(AbstractDocument.ATTRIBUTE_KEY__ID);
        // they should be the same
        assertTrue(newSpreadsheetDocument.equals(spreadsheet_1));
    }

    @Test
    public void testGetSpreadsheetMetadata() {

        String spreadsheet_1_id = TestData.objectId_1.toHexString();

        String spreadsheet_1_json = spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, spreadsheet_1_id);
        BasicDBObject spreadsheet_1_parsedJson = (BasicDBObject) JSON.parse(spreadsheet_1_json);
        BasicDBObject spreadsheet_1_parsedJson_metadataPortion = (BasicDBObject) spreadsheet_1_parsedJson.get(SpreadsheetDocument.ATTRIBUTE_KEY__METADATA);
        String spreadsheet_1_json_metadataPortion = JSON.serialize(spreadsheet_1_parsedJson_metadataPortion);

        String new_json_metadataPortion = spreadsheetBO.getSpreadsheetMetadata(TestMode.TEST_MODE, spreadsheet_1_id);

        // they should be the same
        assertTrue(spreadsheet_1_json_metadataPortion.equals(new_json_metadataPortion));
    }


    @Test
    public void testGetSpreadsheetData() {

        String spreadsheet_1_id = TestData.objectId_1.toHexString();

        String spreadsheet_1_json = spreadsheetBO.getSpreadsheet(TestMode.TEST_MODE, spreadsheet_1_id);
        BasicDBObject spreadsheet_1_parsedJson = (BasicDBObject) JSON.parse(spreadsheet_1_json);
        BasicDBList spreadsheet_1_parsedJson_dataPortion = (BasicDBList) spreadsheet_1_parsedJson.get(SpreadsheetDocument.ATTRIBUTE_KEY__DATA);
        String spreadsheet_1_json_dataPortion = JSON.serialize(spreadsheet_1_parsedJson_dataPortion);

        String new_json_dataPortion = spreadsheetBO.getSpreadsheetData(TestMode.TEST_MODE, spreadsheet_1_id);

        // they should be the same
        assertTrue(spreadsheet_1_json_dataPortion.equals(new_json_dataPortion));
    }
}
