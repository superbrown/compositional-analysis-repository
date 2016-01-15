package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.noCellCollectionApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.noCellCollectionsApproach.nc_RowDAO;
import org.apache.log4j.Logger;

public class nc_RowBO extends AbsRowBO {

    protected static Logger log = Logger.getLogger(nc_RowBO.class);

    public nc_RowBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {
        rowDAO = new nc_RowDAO(getSettings());
    }
}
