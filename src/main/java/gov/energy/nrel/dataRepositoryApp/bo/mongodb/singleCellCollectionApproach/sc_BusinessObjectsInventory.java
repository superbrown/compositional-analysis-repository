package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.UtilsBO;

public class sc_BusinessObjectsInventory extends AbsBusinessObjectsInventory {

    public sc_BusinessObjectsInventory(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    protected void init() {

        datasetBO = new sc_DatasetBO(getDataRepositoryApplication());
        rowBO = new sc_RowBO(getDataRepositoryApplication());

        dataCategoryBO = new DataCategoryBO(getDataRepositoryApplication());
        dataTypeBO = new DataTypeBO(getDataRepositoryApplication());

        fileStorageBO = new FileStorageStorageBO(getDataRepositoryApplication());
        utilsBO = new UtilsBO(getDataRepositoryApplication());

        testDataBO = new sc_TestDataBO(getDataRepositoryApplication());
    }
}
