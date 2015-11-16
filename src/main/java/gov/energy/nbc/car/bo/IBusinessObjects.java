package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.Settings;


public interface IBusinessObjects {

    Settings getSettings(TestMode testMode);

    Settings getSettings();

    Settings getSettings_forUnitTestPurposes();

    IDataCategoryBO getDataCategoryBO();

    IDatasetBO getDatasetBO();

    IPhysicalFileBO getPhysicalFileBO();

    IRowBO getRowBO();

    ITestDataBO getTestDataBO();
}
