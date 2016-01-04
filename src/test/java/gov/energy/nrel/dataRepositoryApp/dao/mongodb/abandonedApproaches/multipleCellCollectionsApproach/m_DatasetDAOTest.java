package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDatasetDAOTest;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;

public class m_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new m_BusinessObjects(settings));
    }
}
