package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IRowCollection;
import org.bson.types.ObjectId;

public interface IDatasetDAO extends IDAO {

    IDatasetDocument getDataset(String id);

    ObjectId add(IDatasetDocument datasetDocument, IRowCollection data)
            throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset;

    IRowDAO getRowDAO();

    IDataCategoryDAO getDataCategoryDAO();
}
