package gov.energy.nrel.dataRepositoryApp.dao.mongodb;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.ITestDataBO;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CompletelyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.dao.exception.PartiallyFailedToPersistDataset;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;

import java.util.UUID;

public abstract class TestUsingTestData {

    static public boolean SUSPEND_DATA_SEEDING = false;
    static public boolean SUSPEND_DATA_CLEANUP = false;

    static final public String UUID_TO_AVOID_COLLISSIONS_IF_MORE_THAN_ONE_CLIENT_IS_RUNNING_UNIT_TESTS;
    static {
        String string = UUID.randomUUID().toString();
        int length = string.length();
        UUID_TO_AVOID_COLLISSIONS_IF_MORE_THAN_ONE_CLIENT_IS_RUNNING_UNIT_TESTS =
                string.substring(length - 4, length);
    }

    public static final String[] DEFAULT_SET_OF_DATA_CATEGORIES = new String[] {"Algae", "ATP3", "Biomass"};

    private DataRepositoryApplication dataRepositoryApplication;

    public TestUsingTestData() {
    }

    public DataRepositoryApplication getDataRepositoryApplication() {
        return dataRepositoryApplication;
    }

    protected abstract IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication);

    public static void beforeClass() {
    }

    public void before() {

        Settings settings = new Settings();

        settings.setMongoDbHost("localhost");
        settings.setMongoDbPort("27017");
        settings.setMongoDatabaseName(
                "data-repository-app_UNIT_TESTING_" +
                UUID_TO_AVOID_COLLISSIONS_IF_MORE_THAN_ONE_CLIENT_IS_RUNNING_UNIT_TESTS);
        settings.setRootDirectoryForUploadedDataFiles("target/test-classes/testData");
        settings.setDefaultSetOfDataCategories(DEFAULT_SET_OF_DATA_CATEGORIES);
        settings.setPerformanceLoggingEnabled(false);

        DataRepositoryApplication dataRepositoryApplication = new DataRepositoryApplication(settings);
        IBusinessObjectsInventory businessObjects = createBusinessObjects(dataRepositoryApplication);
        dataRepositoryApplication.setBusinessObjects(businessObjects);

        this.dataRepositoryApplication = dataRepositoryApplication;

        ITestDataBO testDataBO = getBusinessObjects().getTestDataBO();

        // (just in case it's necessary)
        if (SUSPEND_DATA_CLEANUP == false) {
            testDataBO.removeTestData();
        }

        if (SUSPEND_DATA_SEEDING == false) {
            try {
                testDataBO.seedTestDataInTheDatabase_dataset_1_and_2();
            } catch (PartiallyFailedToPersistDataset e) {
                throw new RuntimeException(e);
            } catch (CompletelyFailedToPersistDataset e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected IBusinessObjectsInventory getBusinessObjects() {

        return getDataRepositoryApplication().getBusinessObjects();
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
