package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBOTest;


public class s_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new s_BusinessObjectsInventory(dataRepositoryApplication);
    }
}
