package gov.energy.nrel.dataRepositoryApp.restEndpoint;


import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;

public class AbstractEndpoints {


    protected void throwExceptionIfCleanupOperationsIsOccurring() throws CleanupOperationIsOccurring {

        if (DataRepositoryApplication.cleanupOperationIsOccurring == true) {
            throw new CleanupOperationIsOccurring();
        }
    }
}
