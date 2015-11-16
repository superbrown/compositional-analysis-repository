package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.mongodb.AbsPerformanceTest;


public class m_PerformanceTest extends AbsPerformanceTest
{
    protected void initializeBusinessObjects() {

        Settings settings = createSettingsForUnitTesting();
        Application.setBusinessObjects(new m_BusinessObjects(settings, settings));
    }
}
