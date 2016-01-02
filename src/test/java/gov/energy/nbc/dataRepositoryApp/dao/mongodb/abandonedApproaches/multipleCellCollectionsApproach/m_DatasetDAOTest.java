package gov.energy.nbc.car.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nbc.car.dao.mongodb.AbsDatasetDAOTest;
import gov.energy.nbc.car.settings.Settings;

public class m_DatasetDAOTest extends AbsDatasetDAOTest{

    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new m_BusinessObjects(settings));
    }
}
