package gov.energy.nbc.car.businessObject;

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
