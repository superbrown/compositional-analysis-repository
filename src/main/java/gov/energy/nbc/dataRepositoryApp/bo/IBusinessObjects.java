package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.settings.ISettings;


public interface IBusinessObjects {

    ISettings getSettings();

    IDataCategoryBO getDataCategoryBO();

    IDatasetBO getDatasetBO();

    IPhysicalFileBO getPhysicalFileBO();

    IRowBO getRowBO();

    ITestDataBO getTestDataBO();

    IDataTypeBO getDataTypeBO();
}
