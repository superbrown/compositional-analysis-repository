package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.UtilsBO;

public class s_BusinessObjectsInventory extends AbsBusinessObjectsInventory {

    public s_BusinessObjectsInventory(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    protected void init() {

        datasetBO = new s_DatasetBO(getDataRepositoryApplication());
        rowBO = new s_RowBO(getDataRepositoryApplication());

        dataCategoryBO = new DataCategoryBO(getDataRepositoryApplication());
        dataTypeBO = new DataTypeBO(getDataRepositoryApplication());

        fileStorageBO = new FileStorageStorageBO(getDataRepositoryApplication());
        utilsBO = new UtilsBO(getDataRepositoryApplication());

        testDataBO = new s_TestDataBO(getDataRepositoryApplication());
    }
}
