package gov.energy.nbc.car.businessObject.singleCellSchemaApproach;

import gov.energy.nbc.car.businessObject.AbsBusinessObjects;
import gov.energy.nbc.car.businessObject.DataCategoryBO;
import gov.energy.nbc.car.businessObject.PhysicalFileBO;

public class s_BusinessObjects extends AbsBusinessObjects {

    public s_BusinessObjects() {

        init();
    }

    protected void init() {

        datasetBO = new s_DatasetBO(settings, settings_forUnitTestPurposes);
        rowBO = new s_RowBO(settings, settings_forUnitTestPurposes);
        dataCategoryBO = new DataCategoryBO(settings, settings_forUnitTestPurposes);
        physicalFileBO = new PhysicalFileBO(settings, settings_forUnitTestPurposes);
        testDataBO = new s_TestDataBO(settings_forUnitTestPurposes);
    }
}
