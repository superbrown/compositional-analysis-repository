package gov.energy.nbc.car.businessService;

import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.dao.DeleteResults;


public class DeletionFailure extends Throwable {

    private final DeleteResults deleteResults;

    public DeletionFailure(DeleteResults deleteResults) {

        this.deleteResults = deleteResults;
    }

    public DeletionFailure(DeleteResult deleteResult) {

        this.deleteResults = new DeleteResults();
        deleteResults.add(deleteResult);
    }
}
