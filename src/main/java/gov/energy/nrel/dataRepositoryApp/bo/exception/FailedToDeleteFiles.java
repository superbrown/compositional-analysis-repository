package gov.energy.nrel.dataRepositoryApp.bo.exception;


public class FailedToDeleteFiles extends Exception {

    public FailedToDeleteFiles() {
    }

    public FailedToDeleteFiles(String message) {
        super(message);
    }

    public FailedToDeleteFiles(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToDeleteFiles(Throwable cause) {
        super(cause);
    }

    public FailedToDeleteFiles(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
