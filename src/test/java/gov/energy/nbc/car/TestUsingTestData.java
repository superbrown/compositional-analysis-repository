package gov.energy.nbc.car;

import gov.energy.nbc.car.businessObject.ITestDataBO;
import gov.energy.nbc.car.businessObject.singleCellCollectionApproach.BusinessObjects;

public class TestUsingTestData {

    public static void beforeClass() {

        Application.setBusinessObjects(new BusinessObjects());
    }

    public void before() {

        ITestDataBO testDataBO = Application.getBusinessObjects().getTestDataBO();

        // (just in case it's necessary)
        testDataBO.removeTestData();
        testDataBO.seedTestDataInTheDatabase_dataset_1_and_2();
    }

    public void after() {

        ITestDataBO testDataBO = Application.getBusinessObjects().getTestDataBO();
        testDataBO.removeTestData();
    }

    public static void afterClass() {

        ITestDataBO testDataBO = Application.getBusinessObjects().getTestDataBO();
        testDataBO.dropTheTestDatabase();
    }
}
