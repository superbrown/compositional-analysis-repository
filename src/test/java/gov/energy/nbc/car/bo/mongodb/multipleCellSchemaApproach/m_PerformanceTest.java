package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.bo.mongodb.AbsRowBOTest;


public class m_PerformanceTest extends AbsRowBOTest
{
    protected void initializeBusinessObject(Settings settings, Settings_forUnitTestPurposes settings_forUnitTestPurposes) {

        Application.setBusinessObjects(new m_BusinessObjects(settings, settings_forUnitTestPurposes));
    }
}
