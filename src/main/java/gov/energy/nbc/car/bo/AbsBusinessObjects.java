package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;

public abstract class AbsBusinessObjects implements IBusinessObjects {

    protected Settings settings;
    protected Settings_forUnitTestPurposes settings_forUnitTestPurposes;

    protected IDataCategoryBO dataCategoryBO;
    protected IDatasetBO datasetBO;
    protected IRowBO rowBO;
    protected IPhysicalFileBO physicalFileBO;
    protected ITestDataBO testDataBO;

    public AbsBusinessObjects(Settings settings, Settings_forUnitTestPurposes settings_forUnitTestPurposes) {

        this.settings = settings;
        this.settings_forUnitTestPurposes = settings_forUnitTestPurposes;

        init();
    }

    protected abstract void init();

    @Override
    public Settings getSettings(TestMode testMode) {

        if (testMode == TestMode.TEST_MODE) {
            return settings_forUnitTestPurposes;
        }
        else {
            return settings;
        }
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public Settings_forUnitTestPurposes getSettings_forUnitTestPurposes() {
        return settings_forUnitTestPurposes;
    }


    @Override
    public IDataCategoryBO getDataCategoryBO() {
        return dataCategoryBO;
    }

    @Override
    public IDatasetBO getDatasetBO() {
        return datasetBO;
    }

    @Override
    public IPhysicalFileBO getPhysicalFileBO() {
        return physicalFileBO;
    }

    @Override
    public IRowBO getRowBO() {
        return rowBO;
    }

    @Override
    public ITestDataBO getTestDataBO() {
        return testDataBO;
    }
}
