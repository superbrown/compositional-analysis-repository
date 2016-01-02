package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import org.bson.types.ObjectId;


public interface ICellDAO extends IDAO {

    IDeleteResults deleteCellsAssociatedWithRow(ObjectId rowId);
}
