package gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.sc_BusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsRowDAOTest;

public class s_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new sc_BusinessObjectsInventory(dataRepositoryApplication);
    }
}
