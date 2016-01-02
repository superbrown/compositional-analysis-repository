package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;

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
