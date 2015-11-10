package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import com.mongodb.client.MongoDatabase;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.ITestDataBO;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach.m_DatasetDAO;
import gov.energy.nbc.car.bo.TestData;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class m_TestDataBO implements ITestDataBO {

    private final Settings settings;
    public IDatasetDAO datasetDAO;

    public m_TestDataBO(Settings settings) {

        this.settings = settings;
        datasetDAO = new m_DatasetDAO(settings);
    }

    public String seedTestDataInTheDatabase_dataset_1_and_2() {

        TestData.dataset_1_objectId = datasetDAO.add(TestData.dataset_1, TestData.rowCollection_1);
        TestData.dataset_2_objectId = datasetDAO.add(TestData.dataset_2, TestData.rowCollection_2);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.dataset_1_objectId);
        newObjects.add(TestData.dataset_2_objectId);

        Document document = new Document().
                append("datasetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    public String seedTestDataInTheDatabase_dataset_1() {

        TestData.dataset_1_objectId = datasetDAO.add(TestData.dataset_1, TestData.rowCollection_1);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.dataset_1_objectId);

        Document document = new Document().
                append("datasetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    public String seedTestDataInTheDatabase_dataset_2() {

        TestData.dataset_2_objectId = datasetDAO.add(TestData.dataset_2, TestData.rowCollection_2);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.dataset_2_objectId);

        Document document = new Document().append("datasetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    public void removeTestData() {

        datasetDAO.getMongoClient().dropDatabase(settings.getMongoDatabaseName());
    }

    public void dropTheTestDatabase() {

        MongoDatabase database = datasetDAO.getDatabase();
        database.drop();
    }
}
