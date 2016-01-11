package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.model.document.IDataCategoryDocument;
import org.bson.types.ObjectId;

import java.util.List;

public interface IDataCategoryDAO extends IDAO {

    IDataCategoryDocument get(String id);

    IDataCategoryDocument get(ObjectId objectId);

    IDataCategoryDocument getByName(Object name);

    List<String> getAllNames();
}
