package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.UtilsBO;

public class mc_BusinessObjectsInventory extends AbsBusinessObjectsInventory {

    public mc_BusinessObjectsInventory(DataRepositoryApplication dataRepositoryApplication) {

        super(dataRepositoryApplication);
    }

    protected void init() {

        datasetBO = new mc_DatasetBO(getDataRepositoryApplication());
        rowBO = new mc_RowBO(getDataRepositoryApplication());

        dataCategoryBO = new DataCategoryBO(getDataRepositoryApplication());
        dataTypeBO = new DataTypeBO(getDataRepositoryApplication());

        fileStorageBO = new FileStorageStorageBO(getDataRepositoryApplication());
        utilsBO = new UtilsBO(getDataRepositoryApplication());

        testDataBO = new mc_TestDataBO(getDataRepositoryApplication());
    }
}
