package gov.energy.nbc.car.dao.mongodb.multipleCellCollectionApproach;

import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.DataCategoryDAO;
import gov.energy.nbc.car.dao.mongodb.IDatasetDAO;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.common.RowCollection;
import gov.energy.nbc.car.model.document.DataCategoryDocument;
import gov.energy.nbc.car.model.document.DatasetDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Set;

public class DatasetDAO_new extends DAO implements IDatasetDAO {

    public static final String COLLECTION_NAME = "dataset";
    protected DataCategoryDAO dataCategoryDAO;
    protected RowDAO_new rowDAO;

    public DatasetDAO_new(ISettings settings) {

        super(COLLECTION_NAME, settings);

        rowDAO = new RowDAO_new(settings);
        dataCategoryDAO = new DataCategoryDAO(settings);
    }

    @Override
    public DatasetDocument getDataset(String id) {

        DatasetDocument datasetDocument = (DatasetDocument) getOneWithId(id);
        return datasetDocument;
    }

    @Override
    public ObjectId add(DatasetDocument datasetDocument, RowCollection data) {

        ObjectId objectId = insert(datasetDocument);

        rowDAO.add(objectId, datasetDocument, data);

        makeSureCollectionIsIndexedForAllColumns(data);

        String dataCategory = datasetDocument.getDataCategory();
        Set columnNames = data.getColumnNames();

        associateColumnNamesToTheDataCategory(dataCategory, columnNames);

        return objectId;
    }

    protected void associateColumnNamesToTheDataCategory(String dataCategory, Set columnNames) {

        DataCategoryDocument dataCategoryDocument = dataCategoryDAO.getByName(dataCategory);

        if (dataCategoryDocument == null) {

            dataCategoryDocument = new DataCategoryDocument();
            dataCategoryDocument.setDataCategory(dataCategory);
            ObjectId objectId = dataCategoryDAO.add(dataCategoryDocument);
            dataCategoryDocument = dataCategoryDAO.get(objectId);
        }

        Set<String> columnNamesFromTheDatabase = dataCategoryDocument.getColumnNames();
        columnNamesFromTheDatabase.addAll(columnNames);

        Document dataToBeUpdated = new Document().
                append(DataCategoryDocument.ATTR_KEY__COLUMN_NAMES, columnNamesFromTheDatabase);

        dataCategoryDAO.updateOne(dataCategoryDocument.getObjectId(), dataToBeUpdated);
    }

    @Override
    public DeleteResults delete(ObjectId objectId) {

        DeleteResults deleteResults = super.delete(objectId);

        if (deleteResults.wasAcknowledged() == false) {
            return deleteResults;
        }

        // FIXME
//        DeleteResults deleteResultsForRows = rowDAO.deleteRowsAssociatedWithDataset(objectId);
//        deleteResults.addAll(deleteResultsForRows);

        return deleteResults;
    }

    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new DatasetDocument(document);
    }

    @Override
    public RowDAO_new getRowDAO() {
        return rowDAO;
    }

    @Override
    public DataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }

    protected void makeSureCollectionIsIndexedForAllColumns(RowCollection rowCollection) {

//        Row firstRow = rowCollection.get(0);
//
//        for (String fieldName : firstRow.keySet()) {
//
//            rowDAO.getCollection().createIndex(new BasicDBObject(fieldName, 1));
//        }
    }
}
