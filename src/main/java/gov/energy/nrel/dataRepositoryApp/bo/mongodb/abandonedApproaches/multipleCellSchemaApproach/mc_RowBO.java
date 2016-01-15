package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach.mc_RowDAO;
import org.apache.log4j.Logger;

public class mc_RowBO extends AbsRowBO {

    protected static Logger log = Logger.getLogger(mc_RowBO.class);

    public mc_RowBO(DataRepositoryApplication settings) {
        super(settings);
    }

    @Override
    protected void init() {
        rowDAO = new mc_RowDAO(getSettings());
    }
}
