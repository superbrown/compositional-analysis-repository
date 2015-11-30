package gov.energy.nbc.car.bo.mongodb.singleRowSchemaApproach.singleCellSchemaApproach;

import gov.energy.nbc.car.bo.AbsBusinessObjects;
import gov.energy.nbc.car.bo.PhysicalFileBO;
import gov.energy.nbc.car.bo.mongodb.DataCategoryBO;
import gov.energy.nbc.car.settings.ISettings;

public class r_BusinessObjects extends AbsBusinessObjects {

    public r_BusinessObjects(ISettings settings) {

        super(settings);
        init();
    }

    protected void init() {

        datasetBO = new r_DatasetBO(settings);
        rowBO = new r_RowBO(settings);
        dataCategoryBO = new DataCategoryBO(settings);
        physicalFileBO = new PhysicalFileBO(settings);
        testDataBO = new r_TestDataBO(settings);
    }
}
