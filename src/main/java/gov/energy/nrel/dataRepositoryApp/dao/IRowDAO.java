package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.bo.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.dto.SearchCriterion;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.common.IRowCollection;
import gov.energy.nrel.dataRepositoryApp.model.document.IRowDocument;
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
