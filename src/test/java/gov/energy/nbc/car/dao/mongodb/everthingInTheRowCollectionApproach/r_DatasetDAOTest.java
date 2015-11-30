package gov.energy.nbc.car.dao.mongodb.everthingInTheRowCollectionApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.singleRowSchemaApproach.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;
import gov.energy.nbc.car.settings.Settings;

public class r_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new r_BusinessObjects(settings));
    }
}
