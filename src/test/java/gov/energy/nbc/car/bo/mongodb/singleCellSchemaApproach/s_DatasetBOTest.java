package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.app.AbsAppConfig;
import gov.energy.nbc.car.settings.Settings;
import gov.energy.nbc.car.bo.mongodb.AbsRowBOTest;
import gov.energy.nbc.car.app.s_AppConfig;


public class s_DatasetBOTest extends AbsRowBOTest
{
    @Override
    protected AbsAppConfig createAppConfig(Settings settings) {
        return new s_AppConfig(settings);
    }
}
