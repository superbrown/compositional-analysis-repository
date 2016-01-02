package gov.energy.nbc.car.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.mongodb.AbsDatasetBOTest;
import gov.energy.nbc.car.settings.Settings;


public class m_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new m_BusinessObjects(settings));
    }
}
