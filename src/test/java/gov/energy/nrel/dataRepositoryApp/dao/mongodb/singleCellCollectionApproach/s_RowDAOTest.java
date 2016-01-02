package gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.app.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsRowDAOTest;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;

public class s_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new s_BusinessObjects(settings));
    }
}
