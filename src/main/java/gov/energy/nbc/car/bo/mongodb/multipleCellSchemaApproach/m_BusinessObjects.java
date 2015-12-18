package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.bo.mongodb.DataTypeBO;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.AbsBusinessObjects;
import gov.energy.nbc.car.bo.FileStorageBO;
import gov.energy.nbc.car.bo.mongodb.DataCategoryBO;

public class m_BusinessObjects extends AbsBusinessObjects {

    public m_BusinessObjects(ISettings settings) {

        super(settings);
    }

    protected void init() {

        datasetBO = new m_DatasetBO(settings);
        rowBO = new m_RowBO(settings);
        dataCategoryBO = new DataCategoryBO(settings);
        physicalFileBO = new FileStorageBO(settings);
        testDataBO = new m_TestDataBO(settings);
        dataTypeBO = new DataTypeBO();
    }
}
