package gov.energy.nrel.dataRepositoryApp.dao.exception;

public class CompletelyFailedToPersistDataset extends Exception {

    public CompletelyFailedToPersistDataset() {
        super();
    }

    public CompletelyFailedToPersistDataset(String message) {
        super(message);
    }

    public CompletelyFailedToPersistDataset(String message, Throwable cause) {
        super(message, cause);
    }

    public CompletelyFailedToPersistDataset(Throwable cause) {
        super(cause);
    }

    protected CompletelyFailedToPersistDataset(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
