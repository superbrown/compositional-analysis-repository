package gov.energy.nbc.car.bo.mongodb.singleRowSchemaApproach.singleCellSchemaApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.AbsPerformanceTest;
import gov.energy.nbc.car.settings.Settings;


public class r_PerformanceTest extends AbsPerformanceTest
{
    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new r_BusinessObjects(settings));
    }

    @Override
    public void testPerformance() {
        // do nothing
    }
}
