package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.dao.dto.IDeleteResults;
import org.bson.types.ObjectId;


public interface ICellDAO extends IDAO {

    IDeleteResults deleteCellsAssociatedWithRow(ObjectId rowId);
}
