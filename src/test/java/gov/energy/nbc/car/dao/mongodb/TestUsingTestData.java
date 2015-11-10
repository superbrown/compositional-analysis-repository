package gov.energy.nbc.car.dao.mongodb;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.bo.ITestDataBO;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;

public class TestUsingTestData {

    static public boolean SUSPEND_DATA_SEEDING = false;
    static public boolean SUSPEND_DATA_CLEANUP = false;

    public static void beforeClass() {

        Application.setBusinessObjects(new s_BusinessObjects(new Settings_forUnitTestPurposes(), new Settings_forUnitTestPurposes()));
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
}
