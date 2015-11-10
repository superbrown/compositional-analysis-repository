package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;


public interface IBusinessObjects {

    Settings getSettings(TestMode testMode);

    Settings getSettings();

    Settings_forUnitTestPurposes getSettings_forUnitTestPurposes();

    IDataCategoryBO getDataCategoryBO();

    IDatasetBO getDatasetBO();

    IPhysicalFileBO getPhysicalFileBO();

    IRowBO getRowBO();

    ITestDataBO getTestDataBO();
}
