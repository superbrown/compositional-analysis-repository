package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DataCategoryAlreadyExists;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataCatogory;
import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Row;
import gov.energy.nrel.dataRepositoryApp.model.document.IDataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.*;

public class DataCategoryBO extends AbsBO implements IDataCategoryBO {

    protected static Logger log = Logger.getLogger(DataCategoryBO.class);

    protected DataCategoryDAO dataCategoryDAO;

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
    }

    @Override
    public String getDataCategory(String dataCategoryId)
            throws UnknownDataCatogory {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDocument(dataCategoryId);
        if (dataCategoryDocument == null) {
            throw new UnknownDataCatogory(dataCategoryId);
        }

        String jsonOut = DAOUtilities.serialize(dataCategoryDocument);
        return jsonOut;
    }

    @Override
    public String getDataCategoryWithName(String name)
            throws UnknownDataCatogory {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(name);
        if (dataCategoryDocument == null) {
            throw new UnknownDataCatogory(name);
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

        Utilities.sortAlphaNumerically(dataCategoryNames);

        String jsonOut = DAOUtilities.serialize(dataCategoryNames);
        return jsonOut;
    }


    @Override
    public String getSearchableColumnNamesForDataCategoryName(String dataCategoryName)
            throws UnknownDataCatogory {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(dataCategoryName);

        if (dataCategoryDocument == null) {
            throw new UnknownDataCatogory(dataCategoryName);
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

    protected IDataCategoryDocument getDataCategoryDocument(String dataCategoryId) {

        IDataCategoryDocument document = getDataCategoryDAO().get(dataCategoryId);
        return document;
    }

    @Override
    public void addDataCategory(String categoryName)
            throws DataCategoryAlreadyExists {

        IDataCategoryDocument dataCategoryDocument = getDataCategoryDAO().getByName(categoryName);

        if (dataCategoryDocument != null) {
            throw new DataCategoryAlreadyExists(categoryName);
        }

        dataCategoryDocument = new DataCategoryDocument();
        dataCategoryDocument.setName(categoryName);

        Set<String> columnNames = new HashSet<>();
        dataCategoryDocument.setColumnNames(columnNames);

        dataCategoryDAO.add(dataCategoryDocument);
    }

    @Override
    public void assureCategoriesAreInTheDatabase(String[] dataCategoryNames) {

        for (String dataCategoryName : dataCategoryNames) {

            try {
                addDataCategory(dataCategoryName);
            }
            catch (DataCategoryAlreadyExists e) {
                // that's fine
            }
        }
    }

    @Override
    public IDataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }
}
