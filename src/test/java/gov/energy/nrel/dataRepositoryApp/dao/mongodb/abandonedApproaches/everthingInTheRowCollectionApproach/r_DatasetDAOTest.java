package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everthingInTheRowCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.app.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDatasetDAOTest;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;

public class r_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new r_BusinessObjects(settings));
    }
}
