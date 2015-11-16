package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.mongodb.AbsRowBOTest;


public class s_DatasetBOTest extends AbsRowBOTest
{
    protected void initializeBusinessObjects() {

        Settings settings = createSettingsForUnitTesting();
        Application.setBusinessObjects(new s_BusinessObjects(settings, settings));
    }
}
