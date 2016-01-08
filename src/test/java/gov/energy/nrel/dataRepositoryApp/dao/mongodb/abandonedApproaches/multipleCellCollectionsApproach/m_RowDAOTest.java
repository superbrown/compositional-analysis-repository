package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach.m_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsRowDAOTest;

public class m_RowDAOTest extends AbsRowDAOTest {

    @Override
    protected IBusinessObjects createBusinessObjects(DataRepositoryApplication dataRepositoryApplication) {

        return new m_BusinessObjects(dataRepositoryApplication);
    }
}
