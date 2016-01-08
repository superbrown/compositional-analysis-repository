package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everythingInTheRowCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsRowDAOTest;

public class r_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected IBusinessObjects createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new r_BusinessObjects(dataRepositoryApplication);
    }
}
