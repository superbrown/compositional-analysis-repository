package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.ITestDataBO;
import gov.energy.nbc.car.settings.Settings;

public abstract class TestUsingTestData {

    static public boolean SUSPEND_DATA_SEEDING = false;
    static public boolean SUSPEND_DATA_CLEANUP = false;

    public static final String[] DEFAULT_SET_OF_DATA_CATEGORIES = new String[] {"Algea", "ATP3", "Biomas"};

    private AppSingleton appSingleton;
    {
        Settings settings = new Settings();

        settings.setMongoDbHost("localhost");
        settings.setMongoDbPort("27017");
        settings.setMongoDatabaseName("car_forUnitTestingPurposes");
        settings.setRootDirectoryForUploadedDataFiles("target/test-classes");
        settings.setDefaultSetOfDataCategories(DEFAULT_SET_OF_DATA_CATEGORIES);

        appSingleton = createAppSingleton(settings);
    }

    protected abstract AppSingleton createAppSingleton(Settings settings);

    public AppSingleton getAppSingleton() {
        return appSingleton;
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

        return getAppSingleton().getBusinessObjects();
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
