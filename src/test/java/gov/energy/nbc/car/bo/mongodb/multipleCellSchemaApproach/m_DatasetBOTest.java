package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.app.AbsAppConfig;
import gov.energy.nbc.car.app.m_AppConfig;
import gov.energy.nbc.car.bo.mongodb.AbsDatasetBOTest;
import gov.energy.nbc.car.settings.Settings;


public class m_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected AbsAppConfig createAppConfig(Settings settings) {
        return new m_AppConfig(settings);
    }
}
