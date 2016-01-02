package gov.energy.nbc.car.bo.exception;

public class UnknownDataset extends Exception{

    public UnknownDataset(String datasetId) {
        super("datasetId: " + datasetId);
    }
}
