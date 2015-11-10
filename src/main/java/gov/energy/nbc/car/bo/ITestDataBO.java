package gov.energy.nbc.car.bo;

/**
 * Created by mbrown on 11/8/2015.
 */
public interface ITestDataBO {
    String seedTestDataInTheDatabase_dataset_1_and_2();

    String seedTestDataInTheDatabase_dataset_1();

    String seedTestDataInTheDatabase_dataset_2();

    void removeTestData();

    void dropTheTestDatabase();
}
