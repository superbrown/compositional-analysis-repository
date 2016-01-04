package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.singleRowSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsPerformanceTest;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;


public class r_PerformanceTest extends AbsPerformanceTest
{
    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new r_BusinessObjects(settings));
    }

    @Override
    public void testPerformance() {
        // do nothing
    }
}
