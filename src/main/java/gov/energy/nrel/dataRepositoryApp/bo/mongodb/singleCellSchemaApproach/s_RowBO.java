package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach.s_RowDAO;
import org.apache.log4j.Logger;

public class s_RowBO extends AbsRowBO {

    protected static Logger log = Logger.getLogger(s_RowBO.class);

    public s_RowBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {
        rowDAO = new s_RowDAO(getSettings());
    }
}
