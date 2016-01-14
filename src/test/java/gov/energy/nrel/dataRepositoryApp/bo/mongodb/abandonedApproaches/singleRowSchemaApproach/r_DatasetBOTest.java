package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.singleRowSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjectsInventory;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBOTest;


public class r_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected IBusinessObjectsInventory createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new r_BusinessObjectsInventory(dataRepositoryApplication);
    }

}
