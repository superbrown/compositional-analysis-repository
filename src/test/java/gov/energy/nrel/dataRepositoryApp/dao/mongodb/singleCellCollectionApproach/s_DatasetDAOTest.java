package gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDatasetDAOTest;

public class s_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected IBusinessObjects createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new s_BusinessObjects(dataRepositoryApplication);
    }
}
