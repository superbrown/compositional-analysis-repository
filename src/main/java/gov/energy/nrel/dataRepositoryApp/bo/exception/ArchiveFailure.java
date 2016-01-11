package gov.energy.nrel.dataRepositoryApp.bo.exception;


public class ArchiveFailure extends Exception {

    private final Object deleteResult;

    public ArchiveFailure(Object deleteResult) {

        this.deleteResult = deleteResult;
    }

    public Object getDeleteResult() {
        return deleteResult;
    }
}
