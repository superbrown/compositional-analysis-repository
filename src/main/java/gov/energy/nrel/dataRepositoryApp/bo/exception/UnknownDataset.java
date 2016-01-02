package gov.energy.nrel.dataRepositoryApp.bo.exception;

public class UnknownDataset extends Exception{

    public UnknownDataset(String datasetId) {
        super("datasetId: " + datasetId);
    }
}
