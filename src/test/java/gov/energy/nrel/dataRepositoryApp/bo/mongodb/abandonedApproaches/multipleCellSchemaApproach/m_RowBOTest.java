package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBOTest;


public class m_RowBOTest extends AbsRowBOTest
{
    @Override
    protected IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new m_BusinessObjectsInventory(dataRepositoryApplication);
    }
}
