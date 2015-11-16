package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.bo.ITestDataBO;

public class TestUsingTestData {

    static public boolean SUSPEND_DATA_SEEDING = false;
    static public boolean SUSPEND_DATA_CLEANUP = false;

    private static final Settings SETTINGS = new Settings_forUnitTestPurposes();

    static {
        SETTINGS.setMongoDbHost("localhost");
        SETTINGS.setMongoDbPort("27017");
        SETTINGS.setMongoDatabaseName("car_forUnitTestPurposes");
        SETTINGS.setRootDirectoryForUploadedDataFiles("target/test-classes");
        SETTINGS.setDefaultSetOfDataCategories(Application.DEFAULT_SET_OF_DATA_CATEGORIES);
    }

    public static void beforeClass() {
    }

    public void before() {

        ITestDataBO testDataBO = Application.getBusinessObjects().getTestDataBO();

        // (just in case it's necessary)
        if (SUSPEND_DATA_CLEANUP == false) {
            testDataBO.removeTestData();
        }

        if (SUSPEND_DATA_SEEDING == false) {
            testDataBO.seedTestDataInTheDatabase_dataset_1_and_2();
        }
    }

    public void after() {

        if (SUSPEND_DATA_CLEANUP == false) {
            ITestDataBO testDataBO = Application.getBusinessObjects().getTestDataBO();
            testDataBO.removeTestData();
        }
    }

    public static void afterClass() {

        if (SUSPEND_DATA_CLEANUP == false) {
            ITestDataBO testDataBO = Application.getBusinessObjects().getTestDataBO();
            testDataBO.dropTheTestDatabase();
        }
    }

    protected Settings createSettingsForUnitTesting() {

        return SETTINGS;
    }
}
