package gov.energy.nrel.dataRepositoryApp.model.mongodb.document;

import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.AbstractDocument;
import gov.energy.nrel.dataRepositoryApp.model.IDataCategoryDocument;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataCategoryDocument extends AbstractDocument implements IDataCategoryDocument {

    public static final String MONGO_KEY__NAME = "name";
    public static final String MONGO_KEY__COLUMN_NAMES = "columnNames";

    public DataCategoryDocument() {
        super();

        setColumnNames(new HashSet<String>());
    }

    public DataCategoryDocument(String json) {
        super(json);
    }

    public DataCategoryDocument(Document document) {
        super(document);
    }

    protected void init(Document document) {

        if (document == null) {
            return;
        }

        initObjectId(document);

        String dataCategory = (String) document.get(MONGO_KEY__NAME);

        List columnNames = (List) document.get(MONGO_KEY__COLUMN_NAMES);;

        Set<String> columnNameSet = new HashSet<>();
        for (Object colomnName : columnNames) {
            columnNameSet.add((String) colomnName);
        }

        init(dataCategory, columnNameSet);
    }

    protected void init(String dataCategory, Set<String> columnNames) {

        setName(dataCategory);
        setColumnNames(columnNames);
    }

    @Override
    public void setName(String dataCategory) {
        put(MONGO_KEY__NAME, dataCategory);
    }

    @Override
    public String getName() {
        return (String) get(MONGO_KEY__NAME);
    }

    @Override
    public void setColumnNames(Set<String> columnNames) {
        put(MONGO_KEY__COLUMN_NAMES, columnNames);
    }

    @Override
    public Set<String> getColumnNames() {

        Set columnNames = (Set) get(MONGO_KEY__COLUMN_NAMES);
        return DAOUtilities.toDocumentsWithClientSideFieldNames(columnNames);
    }
}
