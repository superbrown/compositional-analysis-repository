package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.ResultsMode;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IRowCollection;
import gov.energy.nbc.car.model.IRowDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Set;


public interface IRowDAO extends IDAO {

    IRowDocument get(String id);

    List<ObjectId> add(ObjectId datasetId, IDatasetDocument datasetDocument, IRowCollection data);

    IDeleteResults deleteRowsAssociatedWithDataset(ObjectId datasetId);

//    List<Document> query(String query, String projection);
//
//    List<Document> query(Bson bson, Bson projection);

    List<Document> query(List<SearchCriterion> searchCriteria, ResultsMode resultsMode);

    Set<ObjectId> getIdsOfRowsThatMatch(SearchCriterion searchCriterion);

    ICellDAO getCellDAO(String columnName);
}
