package gov.energy.nbc.car.businessObject;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;

public class BusinessObjects {

    public static Settings settings;
    public static Settings_forUnitTestPurposes settings_forUnitTestPurposes;

    public static SpreadsheetBO spreadsheetBO;
    public static SpreadsheetRowBO spreadsheetRowBO;
    public static SampleTypeBO sampleTypeBO;
    public static DataFileBO dataFileBO;
    public static TestDataBO testDataBO;

    static {
        settings = new Settings();
        settings_forUnitTestPurposes = new Settings_forUnitTestPurposes();

        spreadsheetBO = new SpreadsheetBO(BusinessObjects.settings, BusinessObjects.settings_forUnitTestPurposes);
        spreadsheetRowBO = new SpreadsheetRowBO(BusinessObjects.settings, BusinessObjects.settings_forUnitTestPurposes);
        sampleTypeBO = new SampleTypeBO(BusinessObjects.settings, BusinessObjects.settings_forUnitTestPurposes);
        dataFileBO = new DataFileBO(BusinessObjects.settings, BusinessObjects.settings_forUnitTestPurposes);
        testDataBO = new TestDataBO(BusinessObjects.settings_forUnitTestPurposes);
    }

    public static Settings getSettings(TestMode testMode) {

        if (testMode == TestMode.TEST_MODE) {
            return settings_forUnitTestPurposes;
        }
        else {
            return settings;
        }
    }
}
