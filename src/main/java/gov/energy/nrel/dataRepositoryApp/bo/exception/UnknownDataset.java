package gov.energy.nrel.dataRepositoryApp.bo.exception;

public class UnknownDataset extends Exception {

    private String id;

    public UnknownDataset(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
