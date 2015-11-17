package gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach;

import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.mongodb.AbsRowBO;
import gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach.m_RowDAO;
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
