package gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;

public class s_RowDAOTest extends AbsRowDAOTest {

    protected void initializeBusinessObjects() {

        Application.setBusinessObjects(new s_BusinessObjects());
    }
}
