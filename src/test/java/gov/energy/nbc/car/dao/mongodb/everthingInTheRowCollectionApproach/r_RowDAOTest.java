package gov.energy.nbc.car.dao.mongodb.everthingInTheRowCollectionApproach;

import gov.energy.nbc.car.app.AppSingleton;
import gov.energy.nbc.car.bo.mongodb.singleRowSchemaApproach.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;
import gov.energy.nbc.car.settings.Settings;

public class r_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected AppSingleton createAppSingleton(Settings settings) {

        return new AppSingleton(settings, new r_BusinessObjects(settings));
    }
}
