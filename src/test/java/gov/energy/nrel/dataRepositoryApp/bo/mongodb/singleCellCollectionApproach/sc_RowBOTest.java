package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBOTest;


public class sc_RowBOTest extends AbsRowBOTest
{
    @Override
    protected IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new sc_BusinessObjectsInventory(dataRepositoryApplication);
    }
}
