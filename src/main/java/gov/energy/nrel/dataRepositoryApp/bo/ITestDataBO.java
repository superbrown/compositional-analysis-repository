package gov.energy.nrel.dataRepositoryApp.bo;


import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;

public interface ITestDataBO extends IBO {

    String seedTestDataInTheDatabase_dataset_1_and_2() throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset;

    String seedTestDataInTheDatabase_dataset_1() throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset;

    String seedTestDataInTheDatabase_dataset_2() throws PartiallyFailedToPersistDataset, CompletelyFailedToPersistDataset;

    void removeTestData();

    void dropTheTestDatabase();
}
