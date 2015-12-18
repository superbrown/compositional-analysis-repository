package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.bo.mongodb.DataTypeBO;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.AbsBusinessObjects;
import gov.energy.nbc.car.bo.FileStorageBO;
import gov.energy.nbc.car.bo.mongodb.DataCategoryBO;

public class s_BusinessObjects extends AbsBusinessObjects {

    public s_BusinessObjects(ISettings settings) {

        super(settings);
        init();
    }

    protected void init() {

        datasetBO = new s_DatasetBO(settings);
        rowBO = new s_RowBO(settings);
        dataCategoryBO = new DataCategoryBO(settings);
        physicalFileBO = new FileStorageBO(settings);
        testDataBO = new s_TestDataBO(settings);
        dataTypeBO = new DataTypeBO();
    }
}
