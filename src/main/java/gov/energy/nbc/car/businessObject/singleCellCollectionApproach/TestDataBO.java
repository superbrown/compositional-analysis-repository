package gov.energy.nbc.car.businessObject.singleCellCollectionApproach;

import com.mongodb.client.MongoDatabase;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.IDatasetDAO;
import gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach.DatasetDAO;
import gov.energy.nbc.car.model.TestData;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class TestDataBO implements gov.energy.nbc.car.businessObject.ITestDataBO {

    private final Settings settings;
    public IDatasetDAO datasetDAO;

    public TestDataBO(Settings settings) {

        this.settings = settings;
        datasetDAO = new DatasetDAO(settings);
    }

    @Override
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

    @Override
    public String seedTestDataInTheDatabase_dataset_1() {

        TestData.dataset_1_objectId = datasetDAO.add(TestData.dataset_1, TestData.rowCollection_1);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.dataset_1_objectId);

        Document document = new Document().
                append("datasetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    @Override
    public String seedTestDataInTheDatabase_dataset_2() {

        TestData.dataset_2_objectId = datasetDAO.add(TestData.dataset_2, TestData.rowCollection_2);

        List<ObjectId> newObjects = new ArrayList<>();
        newObjects.add(TestData.dataset_2_objectId);

        Document document = new Document().append("datasetIDs", newObjects);

        return DAOUtilities.serialize(document);
    }

    @Override
    public void removeTestData() {

        datasetDAO.getMongoClient().dropDatabase(settings.getMongoDatabaseName());
    }

    @Override
    public void dropTheTestDatabase() {

        MongoDatabase database = datasetDAO.getDatabase();
        database.drop();
    }
}
