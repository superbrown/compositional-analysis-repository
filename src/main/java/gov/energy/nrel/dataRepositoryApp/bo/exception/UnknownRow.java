package gov.energy.nrel.dataRepositoryApp.bo.exception;

public class UnknownRow extends Exception {

    private String id;

    public UnknownRow(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
