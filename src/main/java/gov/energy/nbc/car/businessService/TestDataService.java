package gov.energy.nbc.car.businessService;

import com.mongodb.client.MongoDatabase;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.dao.SpreadsheetDocumentDAO;
import gov.energy.nbc.car.model.TestData;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class TestDataService {

    public SpreadsheetDocumentDAO spreadsheetDocumentDAO;

    public TestDataService(Settings settings) {

        spreadsheetDocumentDAO = new SpreadsheetDocumentDAO(settings);
    }

    public String seedTestDataInTheDatabase_spreadsheet_1_and_2() {

        TestData.objectId_1 = spreadsheetDocumentDAO.add(TestData.spreadsheetDocument_1);
        TestData.objectId_2 = spreadsheetDocumentDAO.add(TestData.spreadsheetDocument_2);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.objectId_1);
        newObjects.add(TestData.objectId_2);

        Document document = new Document().
                append("spreadsheetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    public String seedTestDataInTheDatabase_spreadsheet_1() {

        TestData.objectId_1 = spreadsheetDocumentDAO.add(TestData.spreadsheetDocument_1);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.objectId_1);

        Document document = new Document().
                append("spreadsheetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    public String seedTestDataInTheDatabase_spreadsheet_2() {

        TestData.objectId_2 = spreadsheetDocumentDAO.add(TestData.spreadsheetDocument_2);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.objectId_2);

        Document document = new Document().append("spreadsheetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    public void removeTestData() {

        spreadsheetDocumentDAO.removeAllDataFromCollection();
    }

    public void dropTheTestDatabase() {

        MongoDatabase database = spreadsheetDocumentDAO.getDatabase();
        database.drop();
    }
}
