package gov.energy.nbc.car.dao.exception;

public class CouldNotCreateDirectory extends Exception {

    public CouldNotCreateDirectory() {
    }

    public CouldNotCreateDirectory(String message) {
        super(message);
    }

    public CouldNotCreateDirectory(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotCreateDirectory(Throwable cause) {
        super(cause);
    }

    public CouldNotCreateDirectory(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
