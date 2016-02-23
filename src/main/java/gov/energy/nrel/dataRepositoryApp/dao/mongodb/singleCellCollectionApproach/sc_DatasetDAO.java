package gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetTransactionTokenDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IRowDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DatasetTransactionTokenDAO;
import gov.energy.nrel.dataRepositoryApp.model.common.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Row;
import gov.energy.nrel.dataRepositoryApp.model.document.IDataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DatasetDocument;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.Set;

public class sc_DatasetDAO extends AbsDAO implements IDatasetDAO
{
    public static final String COLLECTION_NAME = "dataset";
    protected DataCategoryDAO dataCategoryDAO;
    protected IRowDAO rowDAO;
    protected IDatasetTransactionTokenDAO datasetTransactionTokenDAO;

    public sc_DatasetDAO(ISettings settings) {

        super(COLLECTION_NAME, settings);
    }

    @Override
    public void init(String collectionName, ISettings settings) {
        super.init(collectionName, settings);
        rowDAO = new sc_RowDAO(settings);
        dataCategoryDAO = new DataCategoryDAO(settings);
        datasetTransactionTokenDAO = new DatasetTransactionTokenDAO(getSettings());
    }

    public IDatasetDocument getDataset(String id) throws UnknownDataset {

        DatasetDocument datasetDocument = (DatasetDocument) getOneWithId(id);

        if (datasetDocument == null) {
            throw new UnknownDataset(id);
        }

        return datasetDocument;
    }

    public ObjectId add(IDatasetDocument datasetDocument, IRowCollection data)
            throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset {

        ObjectId datasetObjectId = null;
        try {
            datasetObjectId = add(datasetDocument);
        }
        catch (Throwable e) {
            throw new CompletelyFailedToPersistDataset(e);
        }

        try {
            // DESIGN NOTE: This token will be removed by the calling code.  I'm not certain this is the best design,
            // but a constraint we have is that, if an exception is thrown, the calling code is responsible for cleaning
            // up, and we don't want to remove the token until that's accomplished.
            addInWorkTokenToDatabase(datasetObjectId);

            rowDAO.add(datasetObjectId, datasetDocument, data);

            String dataCategory = datasetDocument.getMetadata().getDataCategory();
            Set columnNames = data.getColumnNames();

            Set columnNamesToaAssociateToTheDataCategory = new HashSet<>();

            columnNamesToaAssociateToTheDataCategory.addAll(columnNames);
            // We are removing this one because it treated as a metadata column on the UI, meaning it is searchable as a
            // metadata item, though it is really metadata about the row rather than the dataset.  The purpose of
            // associating column names with the data category is to identify column names that are unique to the category
            // by nature of the data that has been ingested for it.
            columnNamesToaAssociateToTheDataCategory.remove(Row.MONGO_KEY__ROW_NUMBER);

            associateColumnNamesToTheDataCategory(dataCategory, columnNamesToaAssociateToTheDataCategory);
            removeInWorkTokenFromDatabase(datasetObjectId);

            return datasetObjectId;
        }
        catch (Throwable e) {
            throw new PartiallyFailedToPersistDataset(datasetObjectId, e);
        }
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

        IDeleteResults deleteResults = super.delete(objectId);

        IDeleteResults deleteResultsForRows = rowDAO.deleteRowsAssociatedWithDataset(objectId);
        deleteResults.addAll(deleteResultsForRows);

        return deleteResults;
    }

    @Override
    public Document createDocumentOfTypeDAOHandles(Document document) {

        return new DatasetDocument(document);
    }

    public IRowDAO getRowDAO() {
        return rowDAO;
    }

    public IDataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }

    @Override
    public void makeSureTableColumnsIRelyUponAreIndexed() {

    }

    private void removeInWorkTokenFromDatabase(ObjectId datasetObjectId) {
        try {
            datasetTransactionTokenDAO.removeToken(datasetObjectId);
        } catch (UnknownEntity unknownEntity) {
            log.error(unknownEntity);
        }
    }

    private void addInWorkTokenToDatabase(ObjectId datasetObjectId) {
        datasetTransactionTokenDAO.addToken(datasetObjectId);
    }
}
