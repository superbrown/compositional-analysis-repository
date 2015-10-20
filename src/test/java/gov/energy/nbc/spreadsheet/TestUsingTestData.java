package gov.energy.nbc.spreadsheet;

import gov.energy.nbc.spreadsheet.busineessService.BusinessServices;
import gov.energy.nbc.spreadsheet.busineessService.TestDataService;

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
