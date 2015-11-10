package gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.DeleteResults;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.DataCategoryDAO;
import gov.energy.nbc.car.model.IDataCategoryDocument;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IRowCollection;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;
import gov.energy.nbc.car.model.mongodb.document.DatasetDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Set;

public class s_DatasetDAO extends DAO implements IDatasetDAO
{
    public static final String COLLECTION_NAME = "dataset";
    protected DataCategoryDAO dataCategoryDAO;
    protected IRowDAO rowDAO;

    public s_DatasetDAO(Settings settings) {

        super(COLLECTION_NAME, settings);

        rowDAO = new s_RowDAO(settings);
        dataCategoryDAO = new DataCategoryDAO(settings);
    }

    public DatasetDocument getDataset(String id) {

        DatasetDocument metadata = (DatasetDocument) getOneWithId(id);
        return metadata;
    }

    public ObjectId add(IDatasetDocument datasetDocument, IRowCollection data) {

        ObjectId objectId = add(datasetDocument);

        rowDAO.add(objectId, datasetDocument, data);

        makeSureCollectionIsIndexedForAllColumns(data);

        String dataCategory = datasetDocument.getDataCategory();
        Set columnNames = data.getColumnNames();

        associateColumnNamesToTheDataCategory(dataCategory, columnNames);

        return objectId;
    }

    protected void associateColumnNamesToTheDataCategory(String dataCategory, Set columnNames) {

        IDataCategoryDocument dataCategoryDocument = dataCategoryDAO.getByName(dataCategory);

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

        dataCategoryDAO.updateOne(dataCategoryDocument.getId(), dataToBeUpdated);
    }

    @Override
    public DeleteResults delete(ObjectId objectId) {

        DeleteResults deleteResults = super.delete(objectId);

        if (deleteResults.wasAcknowledged() == false) {
            return deleteResults;
        }

        DeleteResults deleteResultsForRows = rowDAO.deleteRowsAssociatedWithDataset(objectId);
        deleteResults.addAll(deleteResultsForRows);

        return deleteResults;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new DatasetDocument(document);
    }

    public IRowDAO getRowDAO() {
        return rowDAO;
    }

    public DataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }

    protected void makeSureCollectionIsIndexedForAllColumns(IRowCollection rowCollection) {

//        Row firstRow = rowCollection.get(0);
//
//        for (String fieldName : firstRow.keySet()) {
//
//            rowDAO.getCollection().createIndex(new BasicDBObject(fieldName, 1));
//        }
    }
}
