package gov.energy.nbc.car.dao.mongodb.everthingInTheRowCollectionApproach;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.mongodb.singleRowSchemaApproach.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;
import gov.energy.nbc.car.settings.Settings;

public class r_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new r_BusinessObjects(settings));
    }
}
