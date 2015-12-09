package gov.energy.nbc.car.bo.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.bo.IRowBO;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.ComparisonOperator;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.model.IRowDocument;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.common.StoredFile;
import gov.energy.nbc.car.model.mongodb.document.RowDocument;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        List<Document> rowDocuments = getRowsAsDocuments(query);

        String json = DAOUtilities.serialize(rowDocuments);
        return json;
    }

    protected List<Document> getRowsAsDocuments(String query) {

        BasicDBList criteria = (BasicDBList) JSON.parse(query);

        List<SearchCriterion> rowSearchCriteria = new ArrayList<>();

        for (Object criterion : criteria) {

            BasicDBObject criterionDocument = (BasicDBObject)criterion;

            String name = (String) criterionDocument.get("name");
            Object value = criterionDocument.get("value");
            ComparisonOperator comparisonOperator = ComparisonOperator.valueOf((String) criterionDocument.get("comparisonOperator"));

            SearchCriterion searchCriterion = new SearchCriterion(name, value, comparisonOperator);

            rowSearchCriteria.add(searchCriterion);
        }

        return getRowDAO().query(rowSearchCriteria);
    }

    @Override
    public String getRowsFlat(String query) {

        List<Document> rowDocuments = getRowsAsDocuments(query);

        BasicDBList rowsFlat = new BasicDBList();

        for (Document document : rowDocuments) {

            Document row = new Document();

            row.put(RowDocument.ATTR_KEY__DATASET_ID, ((ObjectId) document.get(RowDocument.ATTR_KEY__DATASET_ID)).toHexString());

            Document metadata = (Document) document.get(RowDocument.ATTR_KEY__METADATA);

            for (String name : metadata.keySet()) {

                Object value = metadata.get(name);

                if (Metadata.ATTR_KEY__UPLOADED_FILE.equals(name)) {
                    row.put(name, ((Document) value).get(StoredFile.ATTR_KEY__ORIGINAL_FILE_NAME));
                }
                else if (Metadata.ATTR_KEY__ID.equals(name)) {
                    row.put(name, ((ObjectId) value).toHexString());
                }
                else if (Metadata.ATTR_KEY__ATTACHMENTS.equals(name)) {
                    // don't include
                }
                else if (value instanceof Date) {
                    row.put(name, toString((Date) value));
                }
                else {
                    row.put(name, value);
                }
            }

            Document data = (Document) document.get(RowDocument.ATTR_KEY__DATA);

            for (String name : data.keySet()) {
                row.put(name, metadata.get(name));
            }

            rowsFlat.add(row);
        }

        String json = DAOUtilities.serialize(rowsFlat);
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
    public String getRowsAssociatedWithDataset(String datasetId) {

        Document idFilter = new Document().append(
                RowDocument.ATTR_KEY__DATASET_ID, new ObjectId(datasetId));

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

    protected String toString(Date date) {
        String string = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return string;
    }
}
