package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Application;
import gov.energy.nbc.car.bo.AbsRowBOTest;


public class s_RowBOTest extends AbsRowBOTest
{
    protected void initializeBusinessObject() {

        Application.setBusinessObjects(new s_BusinessObjects());
    }
}
