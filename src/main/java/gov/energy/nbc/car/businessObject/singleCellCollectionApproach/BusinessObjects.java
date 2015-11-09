package gov.energy.nbc.car.businessObject.singleCellCollectionApproach;

import gov.energy.nbc.car.businessObject.AbsBusinessObjects;
import gov.energy.nbc.car.businessObject.DataCategoryBO;
import gov.energy.nbc.car.businessObject.PhysicalFileBO;

public class BusinessObjects extends AbsBusinessObjects {

    public BusinessObjects() {

        init();
    }

    protected void init() {

        datasetBO = new DatasetBO(settings, settings_forUnitTestPurposes);
        rowBO = new RowBO(settings, settings_forUnitTestPurposes);
        dataCategoryBO = new DataCategoryBO(settings, settings_forUnitTestPurposes);
        physicalFileBO = new PhysicalFileBO(settings, settings_forUnitTestPurposes);
        testDataBO = new TestDataBO(settings_forUnitTestPurposes);
    }
}
