package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.common.Row;
import org.bson.types.ObjectId;

import java.util.List;


public interface ICellDAO extends IDAO {

    List<ObjectId> add(ObjectId rowId, Row row);

    DeleteResults deleteCellsAssociatedWithRow(ObjectId rowId);
}
