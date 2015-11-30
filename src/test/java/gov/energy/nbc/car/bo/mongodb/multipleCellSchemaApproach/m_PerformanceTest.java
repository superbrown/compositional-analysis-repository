package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.AbsPerformanceTest;
import gov.energy.nbc.car.settings.Settings;


public class m_PerformanceTest extends AbsPerformanceTest
{
    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new m_BusinessObjects(settings));
    }
}
