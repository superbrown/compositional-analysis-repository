package gov.energy.nbc.car.dao.mongodb.multipleCellCollectionsApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.multipleCellCollectionsApproach.BusinessObjects_new;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;

public class DatasetDAO_new_Test extends AbsDatasetDAOTest{

    protected void initializeBusinessObjects() {

        Application.setBusinessObjects(new BusinessObjects_new());
    }
}
