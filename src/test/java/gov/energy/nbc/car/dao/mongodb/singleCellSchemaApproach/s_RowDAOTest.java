package gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.app.AbsAppConfig;
import gov.energy.nbc.car.settings.Settings;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;
import gov.energy.nbc.car.app.s_AppConfig;

public class s_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected AbsAppConfig createAppConfig(Settings settings) {

        return new s_AppConfig(settings);
    }
}
