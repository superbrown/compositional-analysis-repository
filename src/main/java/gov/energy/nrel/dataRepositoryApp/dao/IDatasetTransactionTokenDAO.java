package gov.energy.nrel.dataRepositoryApp.dao;

import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import org.bson.types.ObjectId;

import java.util.List;

public interface IDatasetTransactionTokenDAO extends IDAO {

    void addToken(ObjectId datasetId);

    void removeToken(ObjectId datasetId) throws UnknownEntity;

    List<ObjectId> getDatasetIdsOfAllTokens();
}
