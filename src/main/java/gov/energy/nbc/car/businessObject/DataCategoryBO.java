package gov.energy.nbc.car.businessObject;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.dao.mongodb.DataCategoryDAO;
import gov.energy.nbc.car.fileReader.FileReader;
import gov.energy.nbc.car.model.document.DataCategoryDocument;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

public class DataCategoryBO implements IDataCategoryBO {

    Logger log = Logger.getLogger(this.getClass());

    protected DataCategoryDAO dataCategoryDAO;
    protected DataCategoryDAO dataCategoryDAO_FOR_UNIT_TESTING_PURPOSES;

    protected FileReader fileReader;

    public DataCategoryBO(Settings settings,
                          Settings settings_forUnitTestingPurposes) {

        dataCategoryDAO = new DataCategoryDAO(settings);
        dataCategoryDAO_FOR_UNIT_TESTING_PURPOSES = new DataCategoryDAO(settings_forUnitTestingPurposes);

        fileReader = new FileReader();
    }

    @Override
    public String getDataCategory(TestMode testMode,
                                  String dataCategoryId) {

        DataCategoryDocument dataCategoryDocument = getDataCategoryDocument(testMode, dataCategoryId);
        if (dataCategoryDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getDataCategoryWithName(TestMode testMode,
                                          String name) {

        DataCategoryDocument dataCategoryDocument = getDataCategoryDAO(testMode).getByName(name);
        if (dataCategoryDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getAllDataCategories(TestMode testMode) {

        FindIterable<Document> dataCategoryDocuments = getDataCategoryDAO(testMode).getAll();

        String jsonOut = DAOUtilities.serialize(dataCategoryDocuments);
        return jsonOut;
    }

    @Override
    public long deleteDataCategory(TestMode testMode,
                                   String dataCategoryId) throws DeletionFailure {

        DeleteResults deleteResults = getDataCategoryDAO(testMode).delete(dataCategoryId);

        if (deleteResults.wasAcknowledged() == false) {
            throw new DeletionFailure(deleteResults);
        }

        long numberOfObjectsDeleted = deleteResults.getDeletedCount();
        return numberOfObjectsDeleted;
    }

    @Override
    public String addDataCategory(TestMode testMode,
                                  String jsonIn) {

        DataCategoryDAO dataCategoryDAO = getDataCategoryDAO(testMode);

        DataCategoryDocument dataCategoryDocument = new DataCategoryDocument(jsonIn);
        ObjectId objectId = dataCategoryDAO.add(dataCategoryDocument);

        return objectId.toHexString();
    }

    @Override
    public DataCategoryDocument getDataCategoryDocument(TestMode testMode,
                                                        String dataCategoryId) {

        DataCategoryDocument document = getDataCategoryDAO(testMode).get(dataCategoryId);
        return document;
    }

    @Override
    public DataCategoryDAO getDataCategoryDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return dataCategoryDAO;
        }
        else {
            return dataCategoryDAO_FOR_UNIT_TESTING_PURPOSES;
        }
    }
}
