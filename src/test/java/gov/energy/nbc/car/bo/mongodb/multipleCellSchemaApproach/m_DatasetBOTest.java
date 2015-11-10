package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.AbsRowBOTest;


public class m_DatasetBOTest extends AbsRowBOTest
{
    protected void initializeBusinessObject() {

        Application.setBusinessObjects(new m_BusinessObjects());
    }
}
