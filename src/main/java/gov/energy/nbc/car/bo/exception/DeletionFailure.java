package gov.energy.nbc.car.bo.exception;


public class DeletionFailure extends Exception {

    private final Object deleteResult;

    public DeletionFailure(Object deleteResult) {

        this.deleteResult = deleteResult;
    }

    public Object getDeleteResult() {
        return deleteResult;
    }
}
