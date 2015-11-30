package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.AbsDatasetBOTest;
import gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nbc.car.settings.Settings;


public class s_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new s_BusinessObjects(settings));
    }
}
