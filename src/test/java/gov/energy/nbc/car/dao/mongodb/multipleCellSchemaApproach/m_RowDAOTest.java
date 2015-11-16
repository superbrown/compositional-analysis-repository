package gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;

public class m_RowDAOTest extends AbsRowDAOTest {

    protected void initializeBusinessObjects() {

        Settings settings = createSettingsForUnitTesting();
        Application.setBusinessObjects(new m_BusinessObjects(settings, settings));
    }
}
