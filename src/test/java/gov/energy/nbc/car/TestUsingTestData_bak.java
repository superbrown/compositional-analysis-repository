//package gov.energy.nbc.car;
//
//import gov.energy.nbc.car.businessObject.BusinessObjects;
//import gov.energy.nbc.car.businessObject.TestDataBO;
//
//public class TestUsingTestData {
//
//    protected static TestDataBO testDataBO;
//
//    public static void beforeClass() {
//
//        testDataBO = new TestDataBO(BusinessObjects.settings_forUnitTestPurposes);
//    }
//
//    public void before() {
//
//        // (just in case it's necessary)
//        testDataBO.removeTestData();
//
//        testDataBO.seedTestDataInTheDatabase_dataset_1_and_2();
//    }
//
//    public void after() {
//
//        testDataBO.removeTestData();
//    }
//
//    public static void afterClass() {
//
//        testDataBO.dropTheTestDatabase();
//    }
//}
