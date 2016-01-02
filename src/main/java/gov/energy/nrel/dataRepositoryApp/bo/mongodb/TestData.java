package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.RowCollection;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.DatasetDocument;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *     W A R N I N G !
 *
 * Exercise caution when changing values in this class as unit tests have been written with these values in mind.
 */
public class TestData {

    public static Date date_1;
    public static Date date_2;
    public static Date date_3;
    public static Date date_4;

    public static IRowCollection rowCollection_1;
    public static IDatasetDocument dataset_1;

    public static IRowCollection rowCollection_2;
    public static IDatasetDocument dataset_2;

    public static String dataCategory;

    public static ObjectId dataset_1_objectId;
    public static Date submissionDate_1;
    public static String submitter_1;
    public static String chargeNumber_1;
    public static String projectName_1;
    public static String comments_1;
    public static String nameOfSubdocumentContainingData_1;
    public static IStoredFile sourceDocument_1;
    public static List<IStoredFile> attachments_1;
    public static List<String> tags_1;

    public static ObjectId dataset_2_objectId;
    public static Date submissionDate_2;
    public static String submitter_2;
    public static String chargeNumber_2;
    public static String projectName_2;
    public static String comments_2;
    public static IStoredFile sourceDocument_2;
    public static String nameOfSubdocumentContainingData_2;
    public static List<IStoredFile> attachments_2;
    public static List<String> tags_2;

    public static final String ALGEA = "Algea";

    static {
        try {
            date_1 = new Date();
            Thread.sleep(1);
            date_2 = new Date();
            Thread.sleep(1);
            date_3 = new Date();
            Thread.sleep(1);
            date_4 = new Date();
            Thread.sleep(1);

            dataCategory = ALGEA;

            submissionDate_1 = new Date();
            Thread.sleep(1);
            submitter_1 = "Submitter 1";
            chargeNumber_1 = "Charge Number 1";
            projectName_1 = "Project Name 1";
            comments_1 = "Comment 1";
            tags_1 = Arrays.asList("tag 1", "tag 2", "tag 3");
            sourceDocument_1 = new StoredFile("{ originalFileName: \"Dataset 1.xls\", storageLocation : \"sourceDocuments/2015-10-20_08_12_23_124/Dataset 1.xls\"}");
            nameOfSubdocumentContainingData_1 = "sheet 1";
            attachments_1 = null;

            submissionDate_2 = new Date();
            Thread.sleep(1);
            submitter_2 = "Submitter 2";
            chargeNumber_2 = "Charge Number 3";
            projectName_2 = "Project Name 3";
            comments_2 = "Comment 2";
            tags_2 = Arrays.asList("tag 2", "tag 2", "tag 3");
            sourceDocument_2 = new StoredFile("{ originalFileName: \"Dataset 2.xls\", storageLocation : \"sourceDocuments/2015-10-20_08_02_00_231/Dataset 2.xls\"}");
            nameOfSubdocumentContainingData_2 = "sheet 2";
            attachments_2 = new ArrayList<>();
            attachments_2.add(new StoredFile("{ originalFile1ame: \"name_3\", storageLocation : \"uuid_3\"}"));
            attachments_2.add(new StoredFile("{ originalFileName: \"name_4\", storageLocation : \"uuid_4\"}"));
            attachments_2.add(new StoredFile("{ originalFileName: \"name_5\", storageLocation : \"uuid_5\"}"));

            Object[][] data = new Object[][]{
                    {
                            "Some Column Name",
                            "String Values Column Name",
                            "Date Values Column Name",
                            "Boolean Values Column Name",
                            "Float Values Column Name",
                            "Integer Values Column Name",
                            "Varying Value Types Column Name"},
                    {1, "String 1", date_1, false, 1.11, 1, date_1},
                    {2, "String 2", date_2, true, 3.33, 2, "1000"},
                    {3, "String 3", date_3, false, 3.33, 3, 1000},
                    {3, "String 3", date_3, true, 3.33, 3, 1300.54},
                    {3, "String 3", date_3, true, 3.33, 3, "string value"},
            };
            List<List> dataList = toListOfLists(data);
            rowCollection_1 = new RowCollection(dataList);

            data = new Object[][]{
                    {
                            "Some Column Name",
                            "Date Values Column Name",
                            "Float Values Column Name",
                            "Integer Values Column Name",
                            "Additional Column Name 1",
                            "Additional new Column Name 2"},
                    {1, date_1, 1.22, 1, "a1", "b1"},
                    {2, date_2, 2.11, 2, "a2", "b2"},
                    {3, date_3, 3.33, 3, "a3", "b3"},
                    {4, date_4, 4.55, 4, "a4", "b4"},
            };
            dataList = toListOfLists(data);
            rowCollection_2 = new RowCollection(dataList);

            dataset_1 = new DatasetDocument(
                    dataCategory,
                    submissionDate_1,
                    submitter_1,
                    chargeNumber_1,
                    projectName_1,
                    comments_1,
                    sourceDocument_1,
                    nameOfSubdocumentContainingData_1,
                    attachments_1);

            dataset_2 = new DatasetDocument(
                    dataCategory,
                    submissionDate_2,
                    submitter_2,
                    chargeNumber_2,
                    projectName_2,
                    comments_2,
                    sourceDocument_2,
                    nameOfSubdocumentContainingData_2,
                    attachments_2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<List> toListOfLists(Object[][] array) {

        List<List> listOfLists = new ArrayList();

        for (Object[] item : array) {

            List objectList = Arrays.asList(item);
            listOfLists.add(objectList);
        }

        return listOfLists;
    }
}
