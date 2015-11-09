package gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.singleCellCollectionApproach.BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;

public class RowDAOTest extends AbsRowDAOTest {

    protected void initializeBusinessObjects() {

        Application.setBusinessObjects(new BusinessObjects());
    }
}
