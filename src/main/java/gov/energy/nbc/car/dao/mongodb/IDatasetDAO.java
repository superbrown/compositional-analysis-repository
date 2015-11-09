package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.model.common.RowCollection;
import gov.energy.nbc.car.model.document.DatasetDocument;
import org.bson.types.ObjectId;

public interface IDatasetDAO extends IDAO {

    DatasetDocument getDataset(String id);

    ObjectId add(DatasetDocument datasetDocument, RowCollection data);

    IRowDAO getRowDAO();

    DataCategoryDAO getDataCategoryDAO();
}
