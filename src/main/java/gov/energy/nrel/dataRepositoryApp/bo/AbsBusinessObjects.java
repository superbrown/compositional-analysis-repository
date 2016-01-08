package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;

public abstract class AbsBusinessObjects implements IBusinessObjects {

    private final DataRepositoryApplication dataRepositoryApplication;

    protected IDatasetBO datasetBO;
    protected IRowBO rowBO;

    protected IDataCategoryBO dataCategoryBO;
    protected IDataTypeBO dataTypeBO;

    protected IPhysicalFileBO physicalFileBO;
    protected IUtilsBO utilsBO;

    protected ITestDataBO testDataBO;

    public AbsBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        this.dataRepositoryApplication = dataRepositoryApplication;
        init();
    }

    protected abstract void init();

    @Override
    public DataRepositoryApplication getDataRepositoryApplication() {
        return dataRepositoryApplication;
    }

    @Override
    public ISettings getSettings() {
        return dataRepositoryApplication.getSettings();
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

    @Override
    public IDataTypeBO getDataTypeBO() {
        return dataTypeBO;
    }

    @Override
    public IUtilsBO getUtilsBO() {
        return utilsBO;
    }

    @Override
    public void setUtilsBO(IUtilsBO utilsBO) {
        this.utilsBO = utilsBO;
    }
}
