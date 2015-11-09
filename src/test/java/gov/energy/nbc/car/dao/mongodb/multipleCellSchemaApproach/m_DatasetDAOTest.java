package gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;

public class m_DatasetDAOTest extends AbsDatasetDAOTest{

    protected void initializeBusinessObjects() {

        Application.setBusinessObjects(new m_BusinessObjects());
    }
}
