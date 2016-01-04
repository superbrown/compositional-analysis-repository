package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBOTest;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;


public class s_DatasetBOTest extends AbsDatasetBOTest
{
    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new s_BusinessObjects(settings));
    }
}
