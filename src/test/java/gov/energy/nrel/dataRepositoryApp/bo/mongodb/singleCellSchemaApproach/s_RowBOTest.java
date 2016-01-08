package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBOTest;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach.m_BusinessObjects;


public class s_RowBOTest extends AbsRowBOTest
{
    @Override
    protected IBusinessObjects createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new s_BusinessObjects(dataRepositoryApplication);
    }
}
