package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;


public interface IBusinessObjects {

    DataRepositoryApplication getDataRepositoryApplication();

    ISettings getSettings();

    IDataCategoryBO getDataCategoryBO();

    IDatasetBO getDatasetBO();

    IPhysicalFileBO getPhysicalFileBO();

    IRowBO getRowBO();

    ITestDataBO getTestDataBO();

    IDataTypeBO getDataTypeBO();

    IUtilsBO getUtilsBO();

    void setUtilsBO(IUtilsBO utilsBO);
}
