package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.app.AbsAppConfig;
import gov.energy.nbc.car.settings.Settings;
import gov.energy.nbc.car.bo.mongodb.AbsRowBOTest;
import gov.energy.nbc.car.app.m_AppConfig;


public class m_DatasetBOTest extends AbsRowBOTest
{
    @Override
    protected AbsAppConfig createAppConfig(Settings settings) {
        return new m_AppConfig(settings);
    }
}
