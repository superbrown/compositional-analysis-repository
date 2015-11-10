package gov.energy.nbc.car.dao.dto;


public interface IDeleteResults {

    boolean wasAcknowledged();

    long getDeletedCount();

    void addAll(IDeleteResults deleteResultsForRows);
}
