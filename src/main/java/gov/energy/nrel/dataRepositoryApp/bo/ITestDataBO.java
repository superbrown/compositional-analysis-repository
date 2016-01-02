package gov.energy.nrel.dataRepositoryApp.bo;


public interface ITestDataBO {

    String seedTestDataInTheDatabase_dataset_1_and_2();

    String seedTestDataInTheDatabase_dataset_1();

    String seedTestDataInTheDatabase_dataset_2();

    void removeTestData();

    void dropTheTestDatabase();
}
