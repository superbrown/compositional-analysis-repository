package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach;

import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.model.document.IDataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.common.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DatasetDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Set;

public class m_DatasetDAO extends AbsDAO implements IDatasetDAO {

    public static final String COLLECTION_NAME = "dataset";
    protected DataCategoryDAO dataCategoryDAO;
    protected m_RowDAO rowDAO;

    public m_DatasetDAO(ISettings settings) {

        super(COLLECTION_NAME, settings);
    }

    @Override
    public void init(String collectionName, ISettings settings) {
        super.init(collectionName, settings);
        rowDAO = new m_RowDAO(settings);
        dataCategoryDAO = new DataCategoryDAO(settings);
    }

    @Override
    public IDatasetDocument getDataset(String id) {

        DatasetDocument datasetDocument = (DatasetDocument) getOneWithId(id);
        return datasetDocument;
    }

    @Override
    public ObjectId add(IDatasetDocument datasetDocument, IRowCollection data) {

        ObjectId objectId = add(datasetDocument);

        rowDAO.add(objectId, datasetDocument, data);

        String dataCategory = datasetDocument.getMetadata().getDataCategory();
        Set columnNames = data.getColumnNames();
        associateColumnNamesToTheDataCategory(dataCategory, columnNames);

        return objectId;
    }

    protected void associateColumnNamesToTheDataCategory(String dataCategory, Set columnNames) {

        IDataCategoryDocument dataCategoryDocument = dataCategoryDAO.getByName(dataCategory);

        if (dataCategoryDocument == null) {

            dataCategoryDocument = new DataCategoryDocument();
            dataCategoryDocument.setName(dataCategory);
            ObjectId objectId = dataCategoryDAO.add(dataCategoryDocument);
            dataCategoryDocument = dataCategoryDAO.get(objectId);
        }

        Set<String> columnNamesFromTheDatabase = dataCategoryDocument.getColumnNames();
        columnNamesFromTheDatabase.addAll(columnNames);

        Document dataToBeUpdated = new Document().
                append(DataCategoryDocument.MONGO_KEY__COLUMN_NAMES, columnNamesFromTheDatabase);

        dataCategoryDAO.updateOne(dataCategoryDocument.getId(), dataToBeUpdated);
    }

    @Override
    public IDeleteResults delete(ObjectId objectId)
            throws UnknownEntity {

        IDeleteResults deleteResults = rowDAO.deleteRowsAssociatedWithDataset(objectId);

        IDeleteResults deleteResultForDataset = super.delete(objectId);
        deleteResults.addAll(deleteResultForDataset);

        if (deleteResults.wasAcknowledged() == false) {
            return deleteResults;
        }

        return deleteResults;
    }

    public Document createDocumentOfTypeDAOHandles(Document document) {

        return new DatasetDocument(document);
    }

    @Override
    public m_RowDAO getRowDAO() {
        return rowDAO;
    }

    @Override
    public IDataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }

    @Override
    public void makeSureTableColumnsIRelyUponAreIndexed() {

    }
}
