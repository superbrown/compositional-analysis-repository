package gov.energy.nbc.car;

import gov.energy.nbc.car.busineessService.BusinessServices;
import gov.energy.nbc.car.busineessService.TestDataService;

public class TestUsingTestData {

    private static TestDataService testDataService;

    public static void beforeClass() {

        testDataService = new TestDataService(BusinessServices.settings_forUnitTestPurposes);
    }

    public static void afterClass() {

        testDataService.dropTheTestDatabase();
    }

    public void before() {

        testDataService.removeTestData();
        testDataService.seedTestDataInTheDatabase();
    }

    public void after() {

        testDataService.removeTestData();
    }
}
