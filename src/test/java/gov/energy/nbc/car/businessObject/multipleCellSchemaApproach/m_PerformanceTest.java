package gov.energy.nbc.car.businessObject.multipleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.businessObject.AbsRowBOTest;


public class m_PerformanceTest extends AbsRowBOTest
{
    protected void initializeBusinessObject() {

        Application.setBusinessObjects(new m_BusinessObjects());
    }
}
