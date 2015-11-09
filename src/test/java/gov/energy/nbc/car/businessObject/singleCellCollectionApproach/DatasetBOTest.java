package gov.energy.nbc.car.businessObject.singleCellCollectionApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.AbsDatasetBOTest;


public class DatasetBOTest extends AbsDatasetBOTest
{
    protected void initializeBusinessObject() {

        Application.setBusinessObjects(new BusinessObjects());
    }
}
