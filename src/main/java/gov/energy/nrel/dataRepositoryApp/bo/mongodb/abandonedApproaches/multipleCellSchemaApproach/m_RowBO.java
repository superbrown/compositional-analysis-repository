package gov.energy.nrel.dataRepositoryApp.bo.mongodb.abandonedApproaches.multipleCellSchemaApproach;

import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsRowBO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.multipleCellCollectionsApproach.m_RowDAO;
import org.apache.log4j.Logger;

public class m_RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public m_RowBO(ISettings settings) {
        super(settings);
    }

    public void init(ISettings settings) {

        rowDAO = new m_RowDAO(settings);
    }
}
