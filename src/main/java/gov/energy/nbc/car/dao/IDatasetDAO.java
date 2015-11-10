package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.dao.mongodb.DataCategoryDAO;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IRowCollection;
import gov.energy.nbc.car.model.mongodb.document.DatasetDocument;
import org.bson.types.ObjectId;

public interface IDatasetDAO extends IDAO {

    DatasetDocument getDataset(String id);

    ObjectId add(IDatasetDocument datasetDocument, IRowCollection data);

    IRowDAO getRowDAO();

    DataCategoryDAO getDataCategoryDAO();
}
