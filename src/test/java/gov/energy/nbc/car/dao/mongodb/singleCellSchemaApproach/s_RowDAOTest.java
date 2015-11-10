package gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.Settings_forUnitTestPurposes;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;

public class s_RowDAOTest extends AbsRowDAOTest {

    protected void initializeBusinessObjects(Settings settings, Settings_forUnitTestPurposes settings_forUnitTestPurposes) {

        Application.setBusinessObjects(new s_BusinessObjects(settings, settings_forUnitTestPurposes));
    }
}
