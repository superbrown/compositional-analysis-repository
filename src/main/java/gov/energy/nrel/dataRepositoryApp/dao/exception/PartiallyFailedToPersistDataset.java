package gov.energy.nrel.dataRepositoryApp.dao.exception;

import org.bson.types.ObjectId;

public class PartiallyFailedToPersistDataset extends Exception {

    private final ObjectId datasetObjectId;

    public PartiallyFailedToPersistDataset(ObjectId datasetObjectId, Throwable e) {

        this.datasetObjectId = datasetObjectId;
    }

    public ObjectId getDatasetObjectId() {
        return datasetObjectId;
    }
}
