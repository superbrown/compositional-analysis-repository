package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.UtilsBO;

public class m_BusinessObjects extends AbsBusinessObjects {

    public m_BusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        super(dataRepositoryApplication);
    }

    protected void init() {

        datasetBO = new m_DatasetBO(getDataRepositoryApplication());
        rowBO = new m_RowBO(getDataRepositoryApplication());

        dataCategoryBO = new DataCategoryBO(getDataRepositoryApplication());
        dataTypeBO = new DataTypeBO(getDataRepositoryApplication());

        fileStorageBO = new FileStorageStorageBO(getDataRepositoryApplication());
        utilsBO = new UtilsBO(getDataRepositoryApplication());

        testDataBO = new m_TestDataBO(getDataRepositoryApplication());
    }
}
