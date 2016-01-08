package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.singleRowSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everythingInTheRowCollectionApproach.r_RowDAO;
import org.apache.log4j.Logger;

public class r_RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public r_RowBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {
        rowDAO = new r_RowDAO(getSettings());
    }
}
