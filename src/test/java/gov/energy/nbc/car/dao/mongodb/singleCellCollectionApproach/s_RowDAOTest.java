package gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsRowDAOTest;
import gov.energy.nbc.car.settings.Settings;

public class s_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new s_BusinessObjects(settings));
    }
}
