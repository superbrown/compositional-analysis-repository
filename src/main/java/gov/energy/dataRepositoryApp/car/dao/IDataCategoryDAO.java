package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.model.IDataCategoryDocument;
import org.bson.types.ObjectId;

import java.util.List;

public interface IDataCategoryDAO extends IDAO {

    IDataCategoryDocument get(String id);

    IDataCategoryDocument get(ObjectId objectId);

    IDataCategoryDocument getByName(Object name);

    List<String> getAllNames();
}
