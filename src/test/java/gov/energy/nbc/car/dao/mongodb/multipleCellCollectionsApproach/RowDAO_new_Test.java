package gov.energy.nbc.car.dao.mongodb.multipleCellCollectionsApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.multipleCellCollectionsApproach.BusinessObjects_new;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;

public class RowDAO_new_Test extends AbsRowDAOTest {

    protected void initializeBusinessObjects() {

        Application.setBusinessObjects(new BusinessObjects_new());
    }
}
