package gov.energy.nbc.car.bo.mongodb.singleRowSchemaApproach.singleCellSchemaApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.AbsDatasetBOTest;
import gov.energy.nbc.car.settings.Settings;


public class r_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new r_BusinessObjects(settings));
    }
}
