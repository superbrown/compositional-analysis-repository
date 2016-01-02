package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IRowCollection;
import org.bson.types.ObjectId;

public interface IDatasetDAO extends IDAO {

    IDatasetDocument getDataset(String id);

    ObjectId add(IDatasetDocument datasetDocument, IRowCollection data);

    IRowDAO getRowDAO();

    IDataCategoryDAO getDataCategoryDAO();
}
