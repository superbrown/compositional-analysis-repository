package gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;
import gov.energy.nbc.car.settings.Settings;

public class s_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new s_BusinessObjects(settings));
    }
}
