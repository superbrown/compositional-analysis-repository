package gov.energy.nbc.car.bo.mongodb;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.IDataCategoryBO;
import gov.energy.nbc.car.bo.TestMode;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.DataCategoryDAO;
import gov.energy.nbc.car.model.IDataCategoryDocument;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.IDatasetReader_AllFileTypes;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

public class DataCategoryBO implements IDataCategoryBO {

    Logger log = Logger.getLogger(this.getClass());

    protected DataCategoryDAO dataCategoryDAO;
    protected DataCategoryDAO dataCategoryDAO_FOR_UNIT_TESTING_PURPOSES;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public DataCategoryBO(Settings settings,
                          Settings settings_forUnitTestingPurposes) {

        dataCategoryDAO = new DataCategoryDAO(settings);
        dataCategoryDAO_FOR_UNIT_TESTING_PURPOSES = new DataCategoryDAO(settings_forUnitTestingPurposes);

        generalFileReader = new DatasetReader_AllFileTypes();
    }

    @Override
    public String getDataCategory(TestMode testMode,
                                  String dataCategoryId) {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDocument(testMode, dataCategoryId);
        if (dataCategoryDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getDataCategoryWithName(TestMode testMode,
                                          String name) {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO(testMode).getByName(name);
        if (dataCategoryDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getAllDataCategories(TestMode testMode) {

        Iterable<Document> dataCategoryDocuments = getDataCategoryDAO(testMode).getAll();

        String jsonOut = DAOUtilities.serialize(dataCategoryDocuments);
        return jsonOut;
    }

    @Override
    public void deleteDataCategory(TestMode testMode,
                                   String dataCategoryId) throws DeletionFailure {

        getDataCategoryDAO(testMode).delete(dataCategoryId);
    }

    @Override
    public String addDataCategory(TestMode testMode,
                                  String jsonIn) {

        IDataCategoryDAO dataCategoryDAO = getDataCategoryDAO(testMode);

        DataCategoryDocument dataCategoryDocument = new DataCategoryDocument(jsonIn);
        ObjectId objectId = dataCategoryDAO.add(dataCategoryDocument);

        return objectId.toHexString();
    }

    @Override
    public IDataCategoryDocument getDataCategoryDocument(TestMode testMode,
                                                         String dataCategoryId) {

        IDataCategoryDocument document = getDataCategoryDAO(testMode).get(dataCategoryId);
        return document;
    }

    @Override
    public IDataCategoryDAO getDataCategoryDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return dataCategoryDAO;
        }
        else {
            return dataCategoryDAO_FOR_UNIT_TESTING_PURPOSES;
        }
    }
}
