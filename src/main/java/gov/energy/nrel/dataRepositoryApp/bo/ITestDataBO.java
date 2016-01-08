package gov.energy.nrel.dataRepositoryApp.bo;


public interface ITestDataBO extends IBO {

    String seedTestDataInTheDatabase_dataset_1_and_2();

    String seedTestDataInTheDatabase_dataset_1();

    String seedTestDataInTheDatabase_dataset_2();

    void removeTestData();

    void dropTheTestDatabase();
}
