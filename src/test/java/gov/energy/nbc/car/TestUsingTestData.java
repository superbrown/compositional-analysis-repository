package gov.energy.nbc.car;

import gov.energy.nbc.car.businessService.BusinessServices;
import gov.energy.nbc.car.businessService.TestDataService;

public class TestUsingTestData {

    protected static TestDataService testDataService;

    public static void beforeClass() {

        testDataService = new TestDataService(BusinessServices.settings_forUnitTestPurposes);
    }

    public void before() {

        // (just in case it's necessary)
        testDataService.removeTestData();

        testDataService.seedTestDataInTheDatabase_spreadsheet_1_and_2();
    }

    public void after() {

        testDataService.removeTestData();
    }

    public static void afterClass() {

        testDataService.dropTheTestDatabase();
    }
}
