package gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;

public class s_DatasetDAOTest extends AbsDatasetDAOTest{

    protected void initializeBusinessObjects() {

        Application.setBusinessObjects(new s_BusinessObjects());
    }
}
