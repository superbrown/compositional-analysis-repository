package gov.energy.nrel.dataRepositoryApp.bo.exception;

public class DataCategoryAlreadyExists extends Exception {

    private String id;

    public DataCategoryAlreadyExists(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
