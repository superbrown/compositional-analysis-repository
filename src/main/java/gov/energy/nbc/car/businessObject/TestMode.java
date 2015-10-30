package gov.energy.nbc.car.businessObject;

public enum TestMode {
    TEST_MODE,
    NOT_TEST_MODE;

    public static TestMode value(String testMode) {

        if (testMode == null) {
            return NOT_TEST_MODE;
        }

        // I allow a couple of values here just for user convenience
        if (testMode.equalsIgnoreCase("true") ||
                testMode.equalsIgnoreCase("test") ||
                testMode.equalsIgnoreCase("testmode")) {
            return TEST_MODE;
        }
        else {
            return NOT_TEST_MODE;
        }
    }
}
