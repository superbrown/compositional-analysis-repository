package gov.energy.nrel.dataRepositoryApp.bo.exception;

public class UnknownDataCatogory extends Exception {

    private String id;

    public UnknownDataCatogory(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
