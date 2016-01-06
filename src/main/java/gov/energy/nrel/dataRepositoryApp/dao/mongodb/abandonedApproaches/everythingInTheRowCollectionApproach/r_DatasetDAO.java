package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everythingInTheRowCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.dao.IDataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IRowDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DataCategoryDAO;
import gov.energy.nrel.dataRepositoryApp.model.IDataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.DataCategoryDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.DatasetDocument;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Set;

public class r_DatasetDAO extends AbsDAO implements IDatasetDAO
{
    public static final String COLLECTION_NAME = "dataset";
    protected DataCategoryDAO dataCategoryDAO;
    protected IRowDAO rowDAO;

    public r_DatasetDAO(ISettings settings) {

        super(COLLECTION_NAME, settings);

        rowDAO = new r_RowDAO(settings);
        dataCategoryDAO = new DataCategoryDAO(settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }

    public IDatasetDocument getDataset(String id) {

        DatasetDocument metadata = (DatasetDocument) getOneWithId(id);
        return metadata;
    }

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
                append(DataCategoryDocument.ATTR_KEY__COLUMN_NAMES, columnNamesFromTheDatabase);

        dataCategoryDAO.updateOne(dataCategoryDocument.getId(), dataToBeUpdated);
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new DatasetDocument(document);
    }

    @Override
    public IDeleteResults delete(ObjectId objectId) {

        IDeleteResults deleteResults = super.delete(objectId);

        IDeleteResults deleteResultsForRows = rowDAO.deleteRowsAssociatedWithDataset(objectId);
        deleteResults.addAll(deleteResultsForRows);

        return deleteResults;
    }

    public IRowDAO getRowDAO() {
        return rowDAO;
    }

    public IDataCategoryDAO getDataCategoryDAO() {
        return dataCategoryDAO;
    }


    private static boolean HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = false;

    protected void makeSureTableColumnsIRelyUponAreIndexed() {

        if (HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED == false) {

            getCollection().createIndex(new Document().append(DatasetDocument.ATTR_KEY__ID, 1));
        }
    }
}
