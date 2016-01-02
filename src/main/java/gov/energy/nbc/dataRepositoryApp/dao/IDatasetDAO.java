package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IRowCollection;
import org.bson.types.ObjectId;

public interface IDatasetDAO extends IDAO {

    IDatasetDocument getDataset(String id);

    ObjectId add(IDatasetDocument datasetDocument, IRowCollection data);

    IRowDAO getRowDAO();

    IDataCategoryDAO getDataCategoryDAO();
}
