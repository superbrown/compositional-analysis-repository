package gov.energy.nrel.dataRepositoryApp.dao.dto;


public interface IDeleteResults {

    boolean wasAcknowledged();

    long getDeletedCount();

    void addAll(IDeleteResults deleteResultsForRows);
}
