package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.app.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBOTest;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;


public class m_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new m_BusinessObjects(settings));
    }
}
