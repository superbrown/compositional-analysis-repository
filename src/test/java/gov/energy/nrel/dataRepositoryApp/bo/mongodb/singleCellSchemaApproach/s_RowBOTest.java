package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.mongodb.AbsRowBOTest;
import gov.energy.nbc.car.settings.Settings;


public class s_RowBOTest extends AbsRowBOTest
{
    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new s_BusinessObjects(settings));
    }
}
