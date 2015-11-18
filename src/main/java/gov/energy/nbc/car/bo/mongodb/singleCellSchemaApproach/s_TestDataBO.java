package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import com.mongodb.client.MongoDatabase;
import gov.energy.nbc.car.bo.mongodb.TestData;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.IMongodbDAO;
import gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach.s_DatasetDAO;
import gov.energy.nbc.car.settings.ISettings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class s_TestDataBO implements gov.energy.nbc.car.bo.ITestDataBO {

    private final ISettings settings;
    public IDatasetDAO datasetDAO;

    public s_TestDataBO(ISettings settings) {

        this.settings = settings;
        datasetDAO = new s_DatasetDAO(settings);

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

        ((IMongodbDAO)datasetDAO).getMongoClient().dropDatabase(settings.getMongoDatabaseName());
    }

    @Override
    public void dropTheTestDatabase() {

        MongoDatabase database = ((IMongodbDAO)datasetDAO).getDatabase();
        database.drop();
    }
}
