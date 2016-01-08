package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DataCategoryAlreadyExists;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DeletionFailure;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataCatogory;
import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.model.IDataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Row;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.DataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.IDatasetReader_AllFileTypes;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.*;

public class DataCategoryBO extends AbsBO implements IDataCategoryBO {

    Logger log = Logger.getLogger(this.getClass());

    protected DataCategoryDAO dataCategoryDAO;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public static List<String> DEFAULT_USER_SEARCHABLE_COLUMN_NAMES = new ArrayList<>();
    static {
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Metadata.MONGO_KEY__SUBMISSION_DATE);
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Metadata.MONGO_KEY__SUBMITTER);
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Metadata.MONGO_KEY__PROJECT_NAME);
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Metadata.MONGO_KEY__CHARGE_NUMBER);
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Metadata.MONGO_KEY__COMMENTS);
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Metadata.MONGO_KEY__SOURCE_DOCUMENT);
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Metadata.MONGO_KEY__SUB_DOCUMENT_NAME);
        DEFAULT_USER_SEARCHABLE_COLUMN_NAMES.add(Row.MONGO_KEY__ROW_NUMBER);
    }

    public DataCategoryBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {

        dataCategoryDAO = new DataCategoryDAO(getSettings());
        generalFileReader = new DatasetReader_AllFileTypes();
    }

    @Override
    public String getDataCategory(String dataCategoryId)
            throws UnknownDataCatogory {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDocument(dataCategoryId);
        if (dataCategoryDocument == null) {
            throw new UnknownDataCatogory();
        }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getDataCategoryWithName(String name)
            throws UnknownDataCatogory {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(name);
        if (dataCategoryDocument == null) {
            throw new UnknownDataCatogory();
        }

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
    public String getSearchableColumnNamesForDataCategoryName(String dataCategoryName)
            throws UnknownDataCatogory {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(dataCategoryName);

        if (dataCategoryDocument == null) {
            throw new UnknownDataCatogory();
        }

        Set<String> columnNamesInDataCategory = dataCategoryDocument.getColumnNames();
        columnNamesInDataCategory.remove(Metadata.MONGO_KEY__DATA_CATEGORY);
        // we remove this because it's included in DEFAULT_USER_SEARCHABLE_COLUMN_NAMES
        columnNamesInDataCategory.remove(Row.MONGO_KEY__ROW_NUMBER);

        columnNamesInDataCategory = Utilities.toSortedSet(columnNamesInDataCategory);

        List<String> searchableColumnNames = new ArrayList<>();
        searchableColumnNames.addAll(DEFAULT_USER_SEARCHABLE_COLUMN_NAMES);
        searchableColumnNames.addAll(columnNamesInDataCategory);

        String jsonOut = DAOUtilities.serialize(searchableColumnNames);
        return jsonOut;
    }

    @Override
    public void deleteDataCategory(String dataCategoryId)
            throws DeletionFailure, UnknownDataCatogory {

        try {
            getDataCategoryDAO().delete(dataCategoryId);
        }
        catch (UnknownEntity e) {
            throw new UnknownDataCatogory(e);
        }
    }

    protected IDataCategoryDocument getDataCategoryDocument(String dataCategoryId) {

        IDataCategoryDocument document = getDataCategoryDAO().get(dataCategoryId);
        return document;
    }

    @Override
    public void addDataCategory(String categoryName)
            throws DataCategoryAlreadyExists {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(categoryName);

        if (dataCategoryDocument != null) {
            throw new DataCategoryAlreadyExists();
        }

        dataCategoryDocument = new DataCategoryDocument();
        dataCategoryDocument.setName(categoryName);

        Set<String> columnNames = new HashSet<>();
        dataCategoryDocument.setColumnNames(columnNames);

        dataCategoryDAO.add(dataCategoryDocument);
    }

    @Override
    public IDataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }
}
