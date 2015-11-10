package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.model.IRow;
import org.bson.types.ObjectId;

import java.util.List;


public interface ICellDAO extends IDAO {

    List<ObjectId> add(ObjectId rowId, IRow row);

    IDeleteResults deleteCellsAssociatedWithRow(ObjectId rowId);
}
