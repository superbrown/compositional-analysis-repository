package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsPerformanceTest;


public class s_PerformanceTest extends AbsPerformanceTest
{
    @Override
    protected IBusinessObjects createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new s_BusinessObjects(dataRepositoryApplication);
    }
}
