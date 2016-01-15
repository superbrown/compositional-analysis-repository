package gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellCollectionApproach.sc_BusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsRowDAOTest;

public class sc_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new sc_BusinessObjectsInventory(dataRepositoryApplication);
    }
}
