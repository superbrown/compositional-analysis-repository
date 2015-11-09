package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.businessObject.dto.RowSearchCriteria;
import gov.energy.nbc.car.businessObject.dto.SearchCriterion;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.common.RowCollection;
import gov.energy.nbc.car.model.document.DatasetDocument;
import gov.energy.nbc.car.model.document.RowDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Set;


public interface IRowDAO extends IDAO {

    RowDocument get(String id);

    List<ObjectId> add(ObjectId datasetId, DatasetDocument datasetDocument, RowCollection data);

    DeleteResults deleteRowsAssociatedWithDataset(ObjectId datasetId);

    List<Document> query(String query, String projection);

    List<Document> query(Bson bson, Bson projection);

    List<Document> query(RowSearchCriteria searchCriteria);

    long getCountOfRowsThatMatch(SearchCriterion searchCriterion);

    Set<ObjectId> getIdsOfRowsThatMatch(SearchCriterion searchCriterion);

    ICellDAO getCellDAO(String columnName);
}
