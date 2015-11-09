package gov.energy.nbc.car.businessObject.multipleCellCollectionsApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.AbsDatasetBOTest;


public class DatasetBO_newTest extends AbsDatasetBOTest
{
    protected void initializeBusinessObject() {

        Application.setBusinessObjects(new BusinessObjects_new());
    }
}
