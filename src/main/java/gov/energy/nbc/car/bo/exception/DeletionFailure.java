package gov.energy.nbc.car.bo.exception;

import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.dao.dto.DeleteResults;


public class DeletionFailure extends Exception {

    private final DeleteResults deleteResults;

    public DeletionFailure(DeleteResults deleteResults) {

        this.deleteResults = deleteResults;
    }

    public DeletionFailure(DeleteResult deleteResult) {

        this.deleteResults = new DeleteResults();
        deleteResults.add(deleteResult);
    }
}
