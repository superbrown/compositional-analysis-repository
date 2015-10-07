package gov.energy.nbc.spreadsheet.dao;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.spreadsheet.documents.Metadata;
import gov.energy.nbc.spreadsheet.documents.Spreadsheet;
import gov.energy.nbc.spreadsheet.documents.SpreadsheetRow;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.*;

import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static org.junit.Assert.assertTrue;


public class SampleSpreadsheetsDAOTest
{
    private static Date date_1 = new Date();
    private static Date date_2 = new Date();
    private static Date date_3 = new Date();
    private static Date date_4 = new Date();

    private static SpreadsheetDAO spreadsheetDAO;

    private static Object[][] sampleBiomasSpreadsheetValues_1;
    private static Spreadsheet spreadsheet_1;
    private ObjectId objectId_1;
    private String sampleType_1;
    private String[] tags_1;
    private String spreadsheetPath_1;
    private String[] attachments_1;

    private static Object[][] sampleBiomasSpreadsheetValues_2;
    private static Spreadsheet spreadsheet_2;
    private ObjectId objectId_2;
    private String sampleType_2;
    private String[] tags_2;
    private String spreadsheetPath_2;
    private String[] attachments_2;


    @BeforeClass
    public static void beforeClass() {

        sampleBiomasSpreadsheetValues_1 = new Object[][]{
                {
                        "ID",
                        "String Values Column Name" ,
                        "Date Values Column Name",
                        "Float Values Column Name",
                        "Integer Values Column Name",
                        "Varying Value Types Column Name"},
                {1, "String 1", date_1, 1.11, 1, date_1},
                {2, "String 2", date_2, 3.33, 2, "String"},
                {3, "String 3", date_3, 3.33, 3, 1000},
        };

        sampleBiomasSpreadsheetValues_2 = new Object[][]{
                {
                        "ID",
                        "Date Values Column Name",
                        "Float Values Column Name",
                        "Integer Values Column Name"},
                {1, date_1, 1.22, 1},
                {2, date_2, 2.11, 2},
                {3, date_3, 3.33, 3},
                {4, date_4, 4.55, 4},
        };

        spreadsheetDAO = new SpreadsheetDAO();

        spreadsheetDAO.setCollectionName("test_spreadsheet");
        spreadsheetDAO.getSpreadsheetRowDAO().setCollectionName("test_spreadsheetRow");
    }

    @AfterClass
    public static void afterClass() {

        spreadsheetDAO.getCollection().drop();
        spreadsheetDAO.getSpreadsheetRowDAO().getCollection().drop();
    }

    @Before
    public void before() {

        spreadsheetDAO.deleteAll();


        sampleType_1 = "biomass";
        tags_1 = new String[]{"Charlie", "Lucy", "Linus"};
        spreadsheetPath_1 = "spreadsheet.xls";
        attachments_1 = null;

        spreadsheet_1 = new Spreadsheet(
                sampleType_1,
                tags_1,
                spreadsheetPath_1,
                sampleBiomasSpreadsheetValues_1,
                attachments_1);

        objectId_1 = spreadsheetDAO.add(spreadsheet_1);


        sampleType_2 = sampleType_1;
        tags_2 = new String[]{"Schroeder", "Snoopy"};
        spreadsheetPath_2 = "spreadsheet.xls";
        attachments_2 = new String[]{"attachment 1", "attachment 2"};

        spreadsheet_2 = new Spreadsheet(
                sampleType_2,
                tags_2,
                spreadsheetPath_2,
                sampleBiomasSpreadsheetValues_2,
                attachments_2);

        objectId_2 = spreadsheetDAO.add(spreadsheet_2);
    }

    @After
    public void after() {

        spreadsheetDAO.deleteAll();
    }

    @Test
    public void testGetById() {

        // Confirm the get(ObjectId) method works
        Document document = spreadsheetDAO.get(objectId_1);
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(objectId_1.toString()));
    }

    @Test
    public void testGetByFilter() {

        // Confirm the get() method works
        Document idFilter = new Document();
        idFilter.put("_id", objectId_1);
        List<Document> documents = spreadsheetDAO.get(idFilter);
        assertTrue(documents != null);
        assertTrue(documents.size() == 1);
        assertTrue(documents.get(0).get("_id").toString().equals(objectId_1.toString()));
    }

    @Test
    public void testGetOneByFilter_1() {

        Document idFilter = new Document();
        idFilter.put("_id", objectId_1);
        Document document = spreadsheetDAO.getOne(idFilter);
        assertTrue(document != null);
        assertTrue(document.get("_id").toString().equals(objectId_1.toString()));
    }

    @Test
    public void testGetOneByFilter_2() {

        String attributeKey = spreadsheetDAO.getAttributeKey();

        Bson filter = eq(attributeKey + "." +  SpreadsheetRow.ATTRIBUTE_KEY__DATA + ".String Values Column Name", "String 1");
        List<Document> results = spreadsheetDAO.get(filter);

        assertTrue(results.size() == 1);
    }

    @Test
    public void testGetOneByFilter_3() {

        String attributeKey = spreadsheetDAO.getAttributeKey();

        Bson spreadsheetFilter = and(
                eq(attributeKey + "." + Spreadsheet.ATTRIBUTE_KEY__METADATA + "." + Metadata.ATTRIBUTE_KEY__SAMPLE_TYPE, "biomass"),
                eq(attributeKey + "." + SpreadsheetRow.ATTRIBUTE_KEY__DATA + ".Date Values Column Name", date_2)
        );

        BasicDBObject fieldsToInclude = new BasicDBObject();
        fieldsToInclude.put(attributeKey + "." + Spreadsheet.ATTRIBUTE_KEY__METADATA, 1);
        BasicDBObject projection1 = fieldsToInclude;
        List<Document> results = spreadsheetDAO.get(spreadsheetFilter, projection1);
        assertTrue(results.size() == 2);
    }

    @Test
    public void testThatTheRightNumberOfSpreasheetRowDocumentsExist() {

        int numberOfSpreadsheetRowsThatShouldExist =
                (sampleBiomasSpreadsheetValues_1.length - 1) +
                        (sampleBiomasSpreadsheetValues_2.length - 1);

        long numberOfSpreadsheetRowsThatActuallyExist =
                spreadsheetDAO.getSpreadsheetRowDAO().getCollection().count();

        assertTrue(numberOfSpreadsheetRowsThatActuallyExist == numberOfSpreadsheetRowsThatShouldExist);
    }
}


//            Bson filter = new BasicDBObject("$where", "{ '" + SpreadsheetRow.ATTRIBUTE_KEY__DATA + ".String Values Column Name' : 'String 1' }");
//
//            Bson query = toFilterThatWillOnlyApplyToSpreadsheets(filter);
//            BasicDBObject projection = new BasicDBObject();
//            projection.put(SampleSpreadsheetsDAO.ATTRIBUTE_KEY__SPREAD_SHEET_CONTENT,
//                    new BasicBSONObject(
//                            "$elemMatch",
//                            "'String Values Column Name' : 'String 1'"));

//            System.out.println("** Query: " + JsonUtils.toJson(query));
//            System.out.println("** Projection: " + JsonUtils.toJson(projection));
//
//            List<Document> results = sampleSpreadsheetsDAO.get(
//                    query,
//                    projection);

//            Document filter = Document.parse(
//                    "sampleSpreadsheets.aggregate([\n" +
//                    "    { $match: {\n" +
//                    "     spreadSheetContent: { $elemMatch: {$and: \n" +
//                    "                           [\n" +
//                    "                             {rowNumber: 3}, \n" +
//                    "                             {\"Float Values Column Name\" : 3.33}\n" +
//                    "                           ]} }\n" +
//                    "    }},\n" +
//                    "    { $redact : {\n" +
//                    "         $cond: {\n" +
//                    "             if: { $or : [\n" +
//                    "                           { \n" +
//                    "                             $and: [ { $eq: [\"$rowNumber\", 3] },\n" +
//                    "                                     { $eq: [\"$Float Values Column Name\", 3.33] }\n" +
//                    "                                   ] \n" +
//                    "                           }, \n" +
//                    "                           { \n" +
//                    "                             $not : \"$rowNumber\" \n" +
//                    "                           }\n" +
//                    "                         ]\n" +
//                    "                 },\n" +
//                    "             then: \"$$DESCEND\",\n" +
//                    "             else: \"$$PRUNE\"\n" +
//                    "         }\n" +
//                    "    }}])");
