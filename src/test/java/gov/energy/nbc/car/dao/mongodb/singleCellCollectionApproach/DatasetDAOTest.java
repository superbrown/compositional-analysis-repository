package gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.singleCellCollectionApproach.BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;

public class DatasetDAOTest extends AbsDatasetDAOTest{

    protected void initializeBusinessObjects() {

        Application.setBusinessObjects(new BusinessObjects());
    }
}
