package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everythingInTheRowCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDatasetDAOTest;

public class r_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected IBusinessObjects createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new r_BusinessObjects(dataRepositoryApplication);
    }
}
