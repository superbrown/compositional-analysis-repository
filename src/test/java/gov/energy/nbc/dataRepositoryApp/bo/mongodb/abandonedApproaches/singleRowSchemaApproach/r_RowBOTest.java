package gov.energy.nbc.car.bo.mongodb.abandonedApproaches.singleRowSchemaApproach;

import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.bo.mongodb.AbsRowBOTest;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.r_BusinessObjects;
import gov.energy.nbc.car.settings.Settings;


public class r_RowBOTest extends AbsRowBOTest
{
    @Override
    protected DataRepositoryApplication createAppSingleton(Settings settings) {

        return new DataRepositoryApplication(settings, new r_BusinessObjects(settings));
    }
}
