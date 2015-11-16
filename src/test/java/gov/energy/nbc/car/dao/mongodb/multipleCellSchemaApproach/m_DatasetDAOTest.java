package gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;

public class m_DatasetDAOTest extends AbsDatasetDAOTest{

    protected void initializeBusinessObjects() {

        Settings settings = createSettingsForUnitTesting();
        Application.setBusinessObjects(new m_BusinessObjects(settings, settings));
    }
}
