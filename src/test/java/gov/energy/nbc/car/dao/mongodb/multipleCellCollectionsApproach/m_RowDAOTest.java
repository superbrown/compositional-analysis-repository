package gov.energy.nbc.car.dao.mongodb.multipleCellCollectionsApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;
import gov.energy.nbc.car.settings.Settings;

public class m_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new m_BusinessObjects(settings));
    }
}
