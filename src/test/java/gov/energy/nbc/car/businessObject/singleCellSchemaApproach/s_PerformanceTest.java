package gov.energy.nbc.car.businessObject.singleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.AbsRowBOTest;


public class s_PerformanceTest extends AbsRowBOTest
{
    protected void initializeBusinessObject() {

        Application.setBusinessObjects(new s_BusinessObjects());
    }
}
