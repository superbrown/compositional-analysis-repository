package gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.app.AbsAppConfig;
import gov.energy.nbc.car.settings.Settings;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;
import gov.energy.nbc.car.app.m_AppConfig;

public class m_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected AbsAppConfig createAppConfig(Settings settings) {
        return new m_AppConfig(settings);
    }
}
