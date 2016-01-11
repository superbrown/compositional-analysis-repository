package gov.energy.nrel.dataRepositoryApp.bo.exception;

public class FailedToSave extends Exception {

    public FailedToSave() {
    }

    public FailedToSave(String message) {
        super(message);
    }

    public FailedToSave(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToSave(Throwable cause) {
        super(cause);
    }

    public FailedToSave(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
