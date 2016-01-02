package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;

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
