package gov.energy.nbc.car.bo.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.IRowBO;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.ComparisonOperator;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.model.IRowDocument;
import gov.energy.nbc.car.model.mongodb.document.RowDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsRowBO implements IRowBO {

    protected Logger log = Logger.getLogger(getClass());

    protected IRowDAO rowDAO;

    public AbsRowBO(ISettings settings) {

        init(settings);
    }

    protected abstract void init(ISettings settings);

    @Override
    public String getRow(String rowId) {

        IRowDocument rowDocument = getRowDAO().get(rowId);

        if (rowDocument == null) { return null; }

        String json = DAOUtilities.serialize(rowDocument);
        return json;
    }

    @Override
    public String getRows(String query) {

        BasicDBList list = (BasicDBList)JSON.parse(query);

        List<SearchCriterion> rowSearchCriteria = new ArrayList<>();

        for (Object element : list) {

            BasicDBObject document = (BasicDBObject)element;

            String name = (String) document.get("name");
            Object value = document.get("value");
            ComparisonOperator comparisonOperator = ComparisonOperator.valueOf((String) document.get("comparisonOperator"));

            SearchCriterion searchCriterion = new SearchCriterion(name, value, comparisonOperator);

            rowSearchCriteria.add(searchCriterion);
        }

        List <Document> rowDocuments = getRowDAO().query(rowSearchCriteria);

        if (rowDocuments.size() == 0) { return null; }

        String json = DAOUtilities.serialize(rowDocuments);
        return json;
    }

//    @Override
//    public String getRows(RowSearchCriteria rowSearchCriteria, String projection) {
//
//        List<Document> rowDocuments = getRowDAO(testMode).query(rowSearchCriteria);
//
//        if (rowDocuments.size() == 0) { return null; }
//
//        String json = DAOUtilities.serialize(rowDocuments);
//        return json;
//    }

//    public String getRows(Bson query, Bson projection) {
//
//        List<Document> rowDocuments = getRowDAO(testMode).query(query);
//
//        if (rowDocuments.size() == 0) { return null; }
//
//        String json = DAOUtilities.serialize(rowDocuments);
//        return json;
//    }

    @Override
    public String getRows(List<SearchCriterion> rowSearchCriteria) {

        List<Document> rowDocuments = getRowDAO().query(rowSearchCriteria);

        if (rowDocuments.size() == 0) { return null; }

        String json = DAOUtilities.serialize(rowDocuments);
        return json;
    }

    @Override
    public String getAllRows() {

        Iterable<Document> rowDocuments = getRowDAO().getAll();

        String jsonOut = DAOUtilities.serialize(rowDocuments);
        return jsonOut;
    }


    @Override
    public String getRowAssociatedWithDataset(String datasetId) {

        Document idFilter = new Document().append(
                RowDocument.ATTR_KEY__DATASET_ID, new
                ObjectId(datasetId));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "getRows(testMode).query(" + idFilter.toJson() + ")");
        List<Document> rowDocuments = getRowDAO().get(idFilter, null);
        performanceLogger.done();

        String jsonOut = DAOUtilities.serialize(rowDocuments);
        return jsonOut;
    }


    @Override
    public IRowDAO getRowDAO() {
        return rowDAO;
    }
}
