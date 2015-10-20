package gov.energy.nbc.spreadsheet.busineessService;

import gov.energy.nbc.spreadsheet.Settings;
import gov.energy.nbc.spreadsheet.Settings_forUnitTestPurposes;

public class BusinessServices {

    public static Settings settings;
    public static Settings_forUnitTestPurposes settings_forUnitTestPurposes;

    public static SpreadsheetService spreadsheetService;
    public static SpreadsheetRowService spreadsheetRowsService;
    public static TestDataService testDataService;

    static {
        settings = new Settings();
        settings_forUnitTestPurposes = new Settings_forUnitTestPurposes();

        spreadsheetService = new SpreadsheetService(BusinessServices.settings, BusinessServices.settings_forUnitTestPurposes);
        spreadsheetRowsService = new SpreadsheetRowService(BusinessServices.settings, BusinessServices.settings_forUnitTestPurposes);
        testDataService = new TestDataService(BusinessServices.settings_forUnitTestPurposes);
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
