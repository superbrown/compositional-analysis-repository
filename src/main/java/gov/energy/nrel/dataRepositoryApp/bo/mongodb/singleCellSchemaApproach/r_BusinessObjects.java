package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.bo.AbsBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.FileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.DataTypeBO;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;

public class r_BusinessObjects extends AbsBusinessObjects {

    public r_BusinessObjects(ISettings settings) {

        super(settings);
        init();
    }

    protected void init() {

        datasetBO = new r_DatasetBO(settings);
        rowBO = new r_RowBO(settings);
        dataCategoryBO = new DataCategoryBO(settings);
        physicalFileBO = new FileStorageBO(settings);
        testDataBO = new r_TestDataBO(settings);
        dataTypeBO = new DataTypeBO();
    }
}
