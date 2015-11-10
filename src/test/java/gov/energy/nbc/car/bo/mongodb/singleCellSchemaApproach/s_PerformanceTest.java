package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.bo.mongodb.AbsRowBOTest;


public class s_PerformanceTest extends AbsRowBOTest
{
    protected void initializeBusinessObject(Settings settings, Settings_forUnitTestPurposes settings_forUnitTestPurposes) {

        Application.setBusinessObjects(new s_BusinessObjects(settings, settings_forUnitTestPurposes));
    }
}
