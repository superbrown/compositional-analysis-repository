package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.noCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.UtilsBO;

public class nc_BusinessObjectsInventory extends AbsBusinessObjectsInventory {

    public nc_BusinessObjectsInventory(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    protected void init() {

        datasetBO = new nc_DatasetBO(getDataRepositoryApplication());
        rowBO = new nc_RowBO(getDataRepositoryApplication());

        dataCategoryBO = new DataCategoryBO(getDataRepositoryApplication());
        dataTypeBO = new DataTypeBO(getDataRepositoryApplication());

        fileStorageBO = new FileStorageStorageBO(getDataRepositoryApplication());
        utilsBO = new UtilsBO(getDataRepositoryApplication());

        testDataBO = new nc_TestDataBO(getDataRepositoryApplication());
    }
}
