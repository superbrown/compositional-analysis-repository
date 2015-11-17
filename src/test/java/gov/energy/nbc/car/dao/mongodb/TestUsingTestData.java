package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.app.AbsAppConfig;
import gov.energy.nbc.car.settings.Settings;
import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.ITestDataBO;
import gov.energy.nbc.car.app.TestMode;

public abstract class TestUsingTestData {

    static public boolean SUSPEND_DATA_SEEDING = false;
    static public boolean SUSPEND_DATA_CLEANUP = false;

    private AbsAppConfig appConfig;
    {
        Settings settings = new Settings();

        settings.setMongoDbHost("localhost");
        settings.setMongoDbPort("27017");
        settings.setMongoDatabaseName("car");
        settings.setRootDirectoryForUploadedDataFiles("target/test-classes");
        settings.setDefaultSetOfDataCategories(AbsAppConfig.DEFAULT_SET_OF_DATA_CATEGORIES);

        appConfig = createAppConfig(settings);
    }

    protected abstract AbsAppConfig createAppConfig(Settings settings);

    public AbsAppConfig getAppConfig() {
        return appConfig;
    }

    public static void beforeClass() {
    }

    public void before() {

        ITestDataBO testDataBO = getBusinessObjects().getTestDataBO();

        // (just in case it's necessary)
        if (SUSPEND_DATA_CLEANUP == false) {
            testDataBO.removeTestData();
        }

        if (SUSPEND_DATA_SEEDING == false) {
            testDataBO.seedTestDataInTheDatabase_dataset_1_and_2();
        }
    }

    protected IBusinessObjects getBusinessObjects() {

        return getAppConfig().getBusinessObjects(TestMode.TEST_MODE);
    }

    public void after() {

        if (SUSPEND_DATA_CLEANUP == false) {
            ITestDataBO testDataBO = getBusinessObjects().getTestDataBO();
            testDataBO.dropTheTestDatabase();
        }
    }

    public static void afterClass() {

    }
}
