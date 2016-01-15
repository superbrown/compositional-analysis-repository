package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach.sc_RowDAO;
import org.apache.log4j.Logger;

public class sc_RowBO extends AbsRowBO {

    protected static Logger log = Logger.getLogger(sc_RowBO.class);

    public sc_RowBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {
        rowDAO = new sc_RowDAO(getSettings());
    }
}
