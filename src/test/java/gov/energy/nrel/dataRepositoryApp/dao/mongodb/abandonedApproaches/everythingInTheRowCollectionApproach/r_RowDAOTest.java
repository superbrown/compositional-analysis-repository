package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everythingInTheRowCollectionApproach;


import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.singleRowSchemaApproach.r_BusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsRowDAOTest;

public class r_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new r_BusinessObjectsInventory(dataRepositoryApplication);
    }
}
