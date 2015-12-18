package gov.energy.nbc.car.bo.mongodb;

import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.IDataCategoryBO;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.DataCategoryDAO;
import gov.energy.nbc.car.model.IDataCategoryDocument;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;
import gov.energy.nbc.car.utilities.Utilities;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.IDatasetReader_AllFileTypes;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class DataCategoryBO implements IDataCategoryBO {

    Logger log = Logger.getLogger(this.getClass());

    protected DataCategoryDAO dataCategoryDAO;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public DataCategoryBO(ISettings settings) {

        dataCategoryDAO = new DataCategoryDAO(settings);

        generalFileReader = new DatasetReader_AllFileTypes();
    }

    @Override
    public String getDataCategory(String dataCategoryId) {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDocument(dataCategoryId);
        if (dataCategoryDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getDataCategoryWithName(String name) {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(name);
        if (dataCategoryDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getAllDataCategories() {

        Iterable<Document> dataCategoryDocuments = getDataCategoryDAO().getAll();

        String jsonOut = DAOUtilities.serialize(dataCategoryDocuments);
        return jsonOut;
    }

    @Override
    public String getAllDataCategoryNames() {

        List<String> dataCategoryNames = getDataCategoryDAO().getAllNames();

        // FIXME: This doesn't sort correctly if the string contains a number.  This is a problem with the
        // implementation of String.
        Collections.sort(dataCategoryNames);

        String jsonOut = DAOUtilities.serialize(dataCategoryNames);
        return jsonOut;
    }


    @Override
    public String getColumnNamesForDataCategoryName(String dataCategoryName) {

        IDataCategoryDocument document = getDataCategoryDAO().getByName(dataCategoryName);

        Set<String> columnNames = document.getColumnNames();
        columnNames = Utilities.toSortedSet(columnNames);

        String jsonOut = DAOUtilities.serialize(columnNames);
        return jsonOut;
    }

    @Override
    public void deleteDataCategory(String dataCategoryId) throws DeletionFailure {

        getDataCategoryDAO().delete(dataCategoryId);
    }

    protected IDataCategoryDocument getDataCategoryDocument(String dataCategoryId) {

        IDataCategoryDocument document = getDataCategoryDAO().get(dataCategoryId);
        return document;
    }

    @Override
    public void addDataCategory(String categoryName) {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(categoryName);

        // if it already exists, don't do anything (we don't want duplicates)
        if (dataCategoryDocument != null) {
            return;
        }

        dataCategoryDocument = new DataCategoryDocument();
        dataCategoryDocument.setName(categoryName);

        Set<String> columnNames = new HashSet<>();

        columnNames.add(IMetadata.ATTR_KEY__DATA_CATEGORY);
        columnNames.add(IMetadata.ATTR_KEY__SUBMISSION_DATE);
        columnNames.add(IMetadata.ATTR_KEY__SUBMITTER);
        columnNames.add(IMetadata.ATTR_KEY__PROJECT_NAME);
        columnNames.add(IMetadata.ATTR_KEY__CHARGE_NUMBER);
        columnNames.add(IMetadata.ATTR_KEY__COMMENTS);

        dataCategoryDocument.setColumnNames(columnNames);

        dataCategoryDAO.add(dataCategoryDocument);
    }

    @Override
    public IDataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }
}
