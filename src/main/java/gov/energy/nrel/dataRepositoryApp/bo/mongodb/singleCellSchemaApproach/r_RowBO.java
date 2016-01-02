package gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everthingInTheRowCollectionApproach.r_RowDAO;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import org.apache.log4j.Logger;

public class r_RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public r_RowBO(ISettings settings) {
        super(settings);
    }

    public void init(ISettings settings) {
        rowDAO = new r_RowDAO(settings);
    }
}
