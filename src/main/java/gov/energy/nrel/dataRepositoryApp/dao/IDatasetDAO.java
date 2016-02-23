package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.common.IRowCollection;
import org.bson.types.ObjectId;

public interface IDatasetDAO extends IDAO {

    IDatasetDocument getDataset(String id) throws UnknownDataset;

    ObjectId add(IDatasetDocument datasetDocument, IRowCollection data)
            throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset;

    IRowDAO getRowDAO();

    IDataCategoryDAO getDataCategoryDAO();
}
