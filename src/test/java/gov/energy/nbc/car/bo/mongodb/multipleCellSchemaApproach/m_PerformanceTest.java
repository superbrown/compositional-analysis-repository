package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.mongodb.AbsPerformanceTest;
import gov.energy.nbc.car.settings.Settings;


public class m_PerformanceTest extends AbsPerformanceTest
{
    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new m_BusinessObjects(settings));
    }

    @Override
    public void testPerformance() {
        // do nothing
    }
}
